package com.example.sampleschool.io.repository;

import com.example.sampleschool.io.entity.AwardEntity;
import com.example.sampleschool.io.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AwardRepository extends PagingAndSortingRepository<AwardEntity, Long> {

    List<AwardEntity> findAllByStudentEntityOrderByYearDesc(StudentEntity studentEntity);

    Page<AwardEntity> findAllByStudentEntityOrderByYearDesc(StudentEntity studentEntity, Pageable pageable);

    AwardEntity findByAwardId(String awardId);
}
