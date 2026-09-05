package com.library.service;

import com.library.entity.Notification;
import com.library.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    // ========== 读方法 ==========

    @Cacheable(value = "notifications", key = "'all'")
    public List<Notification> findAll() {
        return notificationMapper.findAll();
    }

    @Cacheable(value = "notifications", key = "'unread'")
    public List<Notification> findUnread() {
        return notificationMapper.findUnread();
    }

    // ========== 写方法 ==========

    @Transactional
    @CacheEvict(value = {"notifications"}, allEntries = true)
    public Notification create(Notification notification) {
        if (notification.getId() == null || notification.getId().isBlank()) {
            notification.setId("notif-" + System.currentTimeMillis());
        }
        if (notification.getTime() == null) {
            notification.setTime("刚刚");
        }
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        notificationMapper.insert(notification);
        return notification;
    }

    @Transactional
    @CacheEvict(value = {"notifications"}, allEntries = true)
    public void markAsRead(String id) {
        notificationMapper.findById(id).ifPresent(notif -> {
            notif.setIsRead(true);
            notificationMapper.update(notif);
        });
    }

    @Transactional
    @CacheEvict(value = {"notifications"}, allEntries = true)
    public void markAllAsRead() {
        notificationMapper.findAll().forEach(notif -> {
            notif.setIsRead(true);
            notificationMapper.update(notif);
        });
    }

    @Transactional
    @CacheEvict(value = {"notifications"}, allEntries = true)
    public void delete(String id) {
        notificationMapper.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"notifications"}, allEntries = true)
    public void deleteAll() {
        notificationMapper.deleteAll();
    }
}
