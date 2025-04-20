package com.spaceobj.spaceobj.mapper;

import com.spaceobj.spaceobj.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {

    Student findById(Long studentId);

    List<Student> findAll();

    List<Student> findByCondition(@Param("name") String name, @Param("age") Integer age);

}
