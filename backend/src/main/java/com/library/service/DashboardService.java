package com.library.service;

import com.library.dto.DashboardStats;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowingMapper;
import com.library.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;
    private final BorrowingMapper borrowingMapper;

    /**
     * 仪表盘统计是高频读、容忍短延迟的典型缓存场景
     * 写操作（book/reader/borrowing 的增删改）都会通过 @CacheEvict 清除此缓存
     * TTL 30 秒（在 RedisConfig.TTL_MAP 中配置）
     */
    @Cacheable(value = "dashboard", key = "'stats'")
    public DashboardStats getStats() {
        long totalBooks = bookMapper.count();
        long inLibrary = bookMapper.countByStatus("available");
        long overdueBooks = bookMapper.countByStatus("overdue");

        long totalReaders = readerMapper.count();
        long premiumReaders = readerMapper.countByLevel("premium");
        int premiumRatio = totalReaders > 0
                ? (int) Math.round((premiumReaders * 100.0) / totalReaders)
                : 0;

        long activeBorrowings = borrowingMapper.count();
        long overdueBorrowings = borrowingMapper.countByStatus("overdue");
        long dueToday = borrowingMapper.countByStatus("due_today");

        return DashboardStats.builder()
                .totalBooks(totalBooks)
                .inLibrary(inLibrary)
                .overdueBooks(overdueBooks)
                .totalReaders(totalReaders)
                .premiumReaders(premiumReaders)
                .premiumRatio(premiumRatio)
                .activeBorrowings(activeBorrowings)
                .overdueBorrowings(overdueBorrowings)
                .dueToday(dueToday)
                .build();
    }
}
