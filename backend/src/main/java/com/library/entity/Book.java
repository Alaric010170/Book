package com.library.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    private String id;
    private String cover;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String status;
}
