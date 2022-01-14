package com.example.sampleschool.io.repository;

import com.example.sampleschool.io.entity.TeacherEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends PagingAndSortingRepository<TeacherEntity, Long> {

    TeacherEntity findTeacherEntitiesByTeacherId(String teacherId);
    TeacherEntity findTeacherEntitiesByEmail(String email);
    TeacherEntity findTeacherEntitiesBySubject(String subject);
}
