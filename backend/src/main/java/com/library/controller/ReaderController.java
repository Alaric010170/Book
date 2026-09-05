package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Reader;
import com.library.service.ReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @GetMapping
    public ApiResponse<List<Reader>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level) {
        if (keyword != null && !keyword.isBlank()) {
            return ApiResponse.success(readerService.search(keyword));
        }
        if (level != null && !level.isBlank()) {
            return ApiResponse.success(readerService.filterByLevel(level));
        }
        return ApiResponse.success(readerService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Reader> getById(@PathVariable String id) {
        return readerService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "读者不存在"));
    }

    @PostMapping
    public ApiResponse<Reader> create(@RequestBody Reader reader) {
        return ApiResponse.success("创建成功", readerService.create(reader));
    }

    @PutMapping("/{id}")
    public ApiResponse<Reader> update(@PathVariable String id, @RequestBody Reader reader) {
        return ApiResponse.success("更新成功", readerService.update(id, reader));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        readerService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
