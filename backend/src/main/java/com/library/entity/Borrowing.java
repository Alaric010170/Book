package com.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrowing {
    private String id;
    private String readerId;
    private String readerName;
    private String readerInitials;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String status;
    private Integer overdueDays;
}
