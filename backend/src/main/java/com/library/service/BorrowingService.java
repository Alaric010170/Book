package com.library.service;

import com.library.entity.Book;
import com.library.entity.Borrowing;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowingMapper;
import com.library.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingMapper borrowingMapper;
    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年M月d日", Locale.CHINESE);

    // ========== 读方法 ==========

    @Cacheable(value = "borrowings", key = "'all'")
    public List<Borrowing> findAll() {
        return borrowingMapper.findAll();
    }

    @Cacheable(value = "borrowings", key = "#keyword == null or #keyword.isBlank() ? 'all' : 'search:' + #keyword")
    public List<Borrowing> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return borrowingMapper.findAll();
        }
        return borrowingMapper.search(keyword);
    }

    @Cacheable(value = "borrowings", key = "#status == null or #status.isBlank() ? 'all' : 'status:' + #status")
    public List<Borrowing> filterByStatus(String status) {
        if (status == null || status.isBlank()) {
            return borrowingMapper.findAll();
        }
        return borrowingMapper.findByStatus(status);
    }

    // ========== 写方法 ==========
    // ⚠️ 关键：BorrowingService 会修改 Book 状态和 Reader 借阅次数，
    // 必须同时清除 books / readers / dashboard 三个缓存，否则前端会看到脏数据

    @Transactional
    @CacheEvict(value = {"borrowings", "books", "book", "readers", "reader", "dashboard"}, allEntries = true)
    public Borrowing create(Borrowing borrowing) {
        if (borrowing.getId() == null || borrowing.getId().isBlank()) {
            borrowing.setId("bor-" + System.currentTimeMillis());
        }

        // Update book status to borrowed
        List<Book> books = bookMapper.search(borrowing.getBookTitle());
        for (Book book : books) {
            if (book.getTitle().equals(borrowing.getBookTitle())
                    && "available".equals(book.getStatus())) {
                book.setStatus("borrowed");
                bookMapper.update(book);
                break;
            }
        }

        // Increment reader borrow count
        readerMapper.findById(borrowing.getReaderId()).ifPresent(reader -> {
            reader.setBorrows(reader.getBorrows() + 1);
            readerMapper.update(reader);
        });

        borrowingMapper.insert(borrowing);
        return borrowing;
    }

    @Transactional
    @CacheEvict(value = {"borrowings", "books", "book", "readers", "reader", "dashboard"}, allEntries = true)
    public void returnBook(String id) {
        borrowingMapper.findById(id).ifPresent(borrowing -> {
            List<Book> books = bookMapper.search(borrowing.getBookTitle());
            for (Book book : books) {
                if (book.getTitle().equals(borrowing.getBookTitle())) {
                    book.setStatus("available");
                    bookMapper.update(book);
                    break;
                }
            }
            borrowingMapper.deleteById(id);
        });
    }

    @Transactional
    @CacheEvict(value = {"borrowings", "dashboard"}, allEntries = true)
    public Borrowing renew(String id) {
        Borrowing borrowing = borrowingMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在: " + id));
        LocalDate newDueDate = LocalDate.now().plusDays(14);
        borrowing.setDueDate(newDueDate.format(FORMATTER));
        borrowing.setStatus("normal");
        borrowing.setOverdueDays(null);
        borrowingMapper.update(borrowing);
        return borrowing;
    }
}
