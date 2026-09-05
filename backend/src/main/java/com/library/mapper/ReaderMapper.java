package com.library.mapper;

import com.library.entity.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReaderMapper {
    List<Reader> findAll();
    Optional<Reader> findById(@Param("id") String id);
    List<Reader> findByLevel(@Param("level") String level);
    List<Reader> search(@Param("keyword") String keyword);
    int insert(Reader reader);
    int update(Reader reader);
    int deleteById(@Param("id") String id);
    long count();
    long countByLevel(@Param("level") String level);
}
