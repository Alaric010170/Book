package com.library.mapper;

import com.library.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdminMapper {
    Optional<Admin> findByUsername(@Param("username") String username);
    int insert(Admin admin);
    long count();
}
