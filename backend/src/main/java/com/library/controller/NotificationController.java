package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.entity.Notification;
import com.library.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> list() {
        return ApiResponse.success(notificationService.findAll());
    }

    @GetMapping("/unread")
    public ApiResponse<List<Notification>> unread() {
        return ApiResponse.success(notificationService.findUnread());
    }

    @PostMapping
    public ApiResponse<Notification> create(@RequestBody Notification notification) {
        return ApiResponse.success("通知创建成功", notificationService.create(notification));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("已标记为已读", null);
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("全部已标记为已读", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteAll() {
        notificationService.deleteAll();
        return ApiResponse.success("全部已清除", null);
    }
}
