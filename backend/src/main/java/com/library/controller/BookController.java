package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Book;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ApiResponse<List<Book>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        if (keyword != null && !keyword.isBlank()) {
            return ApiResponse.success(bookService.search(keyword));
        }
        if (category != null && !category.isBlank()) {
            return ApiResponse.success(bookService.filterByCategory(category));
        }
        if (status != null && !status.isBlank()) {
            return ApiResponse.success(bookService.filterByStatus(status));
        }
        return ApiResponse.success(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Book> getById(@PathVariable String id) {
        return bookService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "图书不存在"));
    }

    @PostMapping
    public ApiResponse<Book> create(@RequestBody Book book) {
        return ApiResponse.success("创建成功", bookService.create(book));
    }

    @PutMapping("/{id}")
    public ApiResponse<Book> update(@PathVariable String id, @RequestBody Book book) {
        return ApiResponse.success("更新成功", bookService.update(id, book));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        bookService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
