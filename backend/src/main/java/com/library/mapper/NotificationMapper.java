package com.library.mapper;

import com.library.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {
    List<Notification> findAll();
    Optional<Notification> findById(@Param("id") String id);
    List<Notification> findUnread();
    int insert(Notification notification);
    int update(Notification notification);
    int deleteById(@Param("id") String id);
    int deleteAll();
    long count();
}
