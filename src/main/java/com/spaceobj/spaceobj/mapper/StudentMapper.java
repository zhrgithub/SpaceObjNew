package com.spaceobj.spaceobj.mapper;

import com.spaceobj.spaceobj.pojo.Student;

import java.util.List;

//@Mapper
public interface StudentMapper {

    Student findById(Long studentId);

    List<Student> findAll();


}
