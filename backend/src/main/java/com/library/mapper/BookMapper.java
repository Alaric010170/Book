package com.library.mapper;

import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BookMapper {
    List<Book> findAll();
    Optional<Book> findById(@Param("id") String id);
    List<Book> findByCategory(@Param("category") String category);
    List<Book> findByStatus(@Param("status") String status);
    List<Book> search(@Param("keyword") String keyword);
    int insert(Book book);
    int update(Book book);
    int deleteById(@Param("id") String id);
    long count();
    long countByStatus(@Param("status") String status);
}
