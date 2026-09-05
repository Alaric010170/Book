package com.library.mapper;

import com.library.entity.Borrowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BorrowingMapper {
    List<Borrowing> findAll();
    Optional<Borrowing> findById(@Param("id") String id);
    List<Borrowing> findByStatus(@Param("status") String status);
    List<Borrowing> search(@Param("keyword") String keyword);
    int insert(Borrowing borrowing);
    int update(Borrowing borrowing);
    int deleteById(@Param("id") String id);
    long count();
    long countByStatus(@Param("status") String status);
}
