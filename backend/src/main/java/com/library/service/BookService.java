package com.library.service;

import com.library.entity.Book;
import com.library.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;

    // ========== 读方法：@Cacheable ==========

    @Cacheable(value = "books", key = "'all'")
    public List<Book> findAll() {
        return bookMapper.findAll();
    }

    @Cacheable(value = "book", key = "#id")
    public Optional<Book> findById(String id) {
        return bookMapper.findById(id);
    }

    /** search(null) 和 search("") 等价，key 统一为 'all' 避免重复缓存 */
    @Cacheable(value = "books", key = "#keyword == null or #keyword.isBlank() ? 'all' : 'search:' + #keyword")
    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return bookMapper.findAll();
        }
        return bookMapper.search(keyword);
    }

    @Cacheable(value = "books", key = "#category == null or #category.isBlank() ? 'all' : 'cat:' + #category")
    public List<Book> filterByCategory(String category) {
        if (category == null || category.isBlank()) {
            return bookMapper.findAll();
        }
        return bookMapper.findByCategory(category);
    }

    @Cacheable(value = "books", key = "#status == null or #status.isBlank() ? 'all' : 'status:' + #status")
    public List<Book> filterByStatus(String status) {
        if (status == null || status.isBlank()) {
            return bookMapper.findAll();
        }
        return bookMapper.findByStatus(status);
    }

    // ========== 写方法：@CacheEvict ==========
    // 任何写操作都清空整个 books 列表缓存 + dashboard 缓存，保证数据一致

    @Transactional
    @CacheEvict(value = {"books", "dashboard"}, allEntries = true)
    public Book create(Book book) {
        if (book.getId() == null || book.getId().isBlank()) {
            book.setId("b" + System.currentTimeMillis());
        }
        bookMapper.insert(book);
        return book;
    }

    @Transactional
    @CacheEvict(value = {"books", "book", "dashboard"}, allEntries = true)
    public Book update(String id, Book updated) {
        Book existing = bookMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("图书不存在: " + id));
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getAuthor() != null) existing.setAuthor(updated.getAuthor());
        if (updated.getIsbn() != null) existing.setIsbn(updated.getIsbn());
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        if (updated.getCover() != null) existing.setCover(updated.getCover());
        bookMapper.update(existing);
        return existing;
    }

    @Transactional
    @CacheEvict(value = {"books", "book", "dashboard"}, allEntries = true)
    public void delete(String id) {
        bookMapper.deleteById(id);
    }
}
