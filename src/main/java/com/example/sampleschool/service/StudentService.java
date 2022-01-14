package com.example.sampleschool.service;

import com.example.sampleschool.shared.dto.StudentDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface StudentService extends UserDetailsService {

    StudentDto createStudent(StudentDto studentDto);

    StudentDto getStudentLoginDetails(String regNo);

    StudentDto getStudent(String regNo);

    StudentDto updateStudent(String regNo, StudentDto studentDto);

    List<StudentDto> getStudents(int page, int limit, String course);

    void deleteStudent(String regNo);

    boolean verifyEmailToken(String token);

    boolean requestPasswordReset(String email);

    boolean resetPassword(String token, String password);
}
