package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Borrowing;
import com.library.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

    private final BorrowingService borrowingService;

    @GetMapping
    public ApiResponse<List<Borrowing>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        if (keyword != null && !keyword.isBlank()) {
            return ApiResponse.success(borrowingService.search(keyword));
        }
        if (status != null && !status.isBlank()) {
            return ApiResponse.success(borrowingService.filterByStatus(status));
        }
        return ApiResponse.success(borrowingService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Borrowing> getById(@PathVariable String id) {
        return borrowingService.findAll().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "借阅记录不存在"));
    }

    @PostMapping
    public ApiResponse<Borrowing> create(@RequestBody Borrowing borrowing) {
        return ApiResponse.success("借阅登记成功", borrowingService.create(borrowing));
    }

    @PostMapping("/{id}/return")
    public ApiResponse<Void> returnBook(@PathVariable String id) {
        borrowingService.returnBook(id);
        return ApiResponse.success("还书成功", null);
    }

    @PostMapping("/{id}/renew")
    public ApiResponse<Borrowing> renew(@PathVariable String id) {
        return ApiResponse.success("续借成功", borrowingService.renew(id));
    }
}
