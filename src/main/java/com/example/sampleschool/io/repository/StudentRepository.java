package com.example.sampleschool.io.repository;

import com.example.sampleschool.io.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends PagingAndSortingRepository<StudentEntity, Long> {

    StudentEntity findStudentEntitiesByRegNo(String regNo);

    StudentEntity findStudentEntitiesByEmail(String email);

    @Query(value = "SELECT * from student_tb s WHERE s.course = :course", nativeQuery = true)
    List<StudentEntity> findAllByCourse(@Param("course") String course);

    @Query(value = "SELECT * FROM student_tb s WHERE s.course LIKE '%:course%'", nativeQuery = true)
    Page<StudentEntity> findAllByCourseLike(@Param("course") String course, Pageable pageable);

    Page<StudentEntity> findAllByCourseContaining(@Param("course") String course, Pageable pageable);

    StudentEntity findStudentEntityByEmailVerificationToken(String token);

}
