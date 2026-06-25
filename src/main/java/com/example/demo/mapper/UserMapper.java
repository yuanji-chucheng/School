package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    User findById(Long id);
    User findByStudentId(String studentId);
    int insert(User user);
    int update(User user);
    List<User> findPending(@Param("offset") int offset, @Param("limit") int limit);
    long countPending();
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    List<User> findApprovedStudents(@Param("offset") int offset, @Param("limit") int limit);
    long countApprovedStudents();
    int deleteById(Long id);
    int deleteBatch(@Param("ids") List<Long> ids);
}
