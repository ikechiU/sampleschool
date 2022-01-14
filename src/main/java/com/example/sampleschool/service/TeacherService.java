package com.example.sampleschool.service;

import com.example.sampleschool.shared.dto.TeacherDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface TeacherService extends UserDetailsService {

    TeacherDto createTeacher(TeacherDto teacherDto);

    TeacherDto getTeacherLoginDetails(String teacherId);

    TeacherDto getTeacher(String teacherId);

    TeacherDto updateTeacher(String teacherId, TeacherDto teacherDto);

    List<TeacherDto> getTeachers(int page, int limit);

    void deleteTeacher(String teacherId);
}
