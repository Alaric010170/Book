package com.library.service;

import com.library.entity.Reader;
import com.library.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderMapper readerMapper;

    // ========== 读方法 ==========

    @Cacheable(value = "readers", key = "'all'")
    public List<Reader> findAll() {
        return readerMapper.findAll();
    }

    @Cacheable(value = "reader", key = "#id")
    public Optional<Reader> findById(String id) {
        return readerMapper.findById(id);
    }

    @Cacheable(value = "readers", key = "#keyword == null or #keyword.isBlank() ? 'all' : 'search:' + #keyword")
    public List<Reader> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return readerMapper.findAll();
        }
        return readerMapper.search(keyword);
    }

    @Cacheable(value = "readers", key = "#level == null or #level.isBlank() ? 'all' : 'level:' + #level")
    public List<Reader> filterByLevel(String level) {
        if (level == null || level.isBlank()) {
            return readerMapper.findAll();
        }
        return readerMapper.findByLevel(level);
    }

    // ========== 写方法 ==========

    @Transactional
    @CacheEvict(value = {"readers", "dashboard"}, allEntries = true)
    public Reader create(Reader reader) {
        if (reader.getId() == null || reader.getId().isBlank()) {
            reader.setId("#MEM-" + (1000 + (int) (Math.random() * 9000)));
        }
        if (reader.getBorrows() == null) {
            reader.setBorrows(0);
        }
        readerMapper.insert(reader);
        return reader;
    }

    @Transactional
    @CacheEvict(value = {"readers", "reader", "dashboard"}, allEntries = true)
    public Reader update(String id, Reader updated) {
        Reader existing = readerMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("读者不存在: " + id));
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getLevel() != null) existing.setLevel(updated.getLevel());
        if (updated.getBorrows() != null) existing.setBorrows(updated.getBorrows());
        if (updated.getAvatar() != null) existing.setAvatar(updated.getAvatar());
        readerMapper.update(existing);
        return existing;
    }

    @Transactional
    @CacheEvict(value = {"readers", "reader", "dashboard"}, allEntries = true)
    public void delete(String id) {
        readerMapper.deleteById(id);
    }
}
