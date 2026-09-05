package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {
    private long totalBooks;
    private long inLibrary;
    private long overdueBooks;
    private long totalReaders;
    private long premiumReaders;
    private int premiumRatio;
    private long activeBorrowings;
    private long overdueBorrowings;
    private long dueToday;
}
