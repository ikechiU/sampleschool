package com.example.sampleschool.service.impl;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.io.entity.AwardEntity;
import com.example.sampleschool.io.entity.StudentEntity;
import com.example.sampleschool.io.repository.AwardRepository;
import com.example.sampleschool.io.repository.StudentRepository;
import com.example.sampleschool.io.repository.TeacherRepository;
import com.example.sampleschool.service.AwardService;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.AwardDto;
import com.example.sampleschool.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    AwardRepository awardRepository;

    @Autowired
    Utils utils;

    @Autowired
    StudentServiceImpl studentService;


    @Override
    public AwardDto createAward(String regNo, AwardDto awardDto) {
        StudentEntity studentEntity = checkStudentEntity(regNo);

        ModelMapper modelMapper = new ModelMapper();

        AwardEntity awardEntity = modelMapper.map(awardDto, AwardEntity.class);

        awardEntity.setAwardId(utils.generateAwardId(15));
        awardEntity.setStudentEntity(studentEntity);

        AwardEntity createdAward = awardRepository.save(awardEntity);

        return modelMapper.map(createdAward, AwardDto.class);
    }

    @Override
    public AwardDto getAward(String regNo, String awardId) {
        StudentEntity studentEntity = checkStudentEntity(regNo);

        AwardEntity awardEntity = checkAwardEntity(awardId);

        if (studentEntity != awardEntity.getStudentEntity()){
            throw new SchoolServiceException(ErrorMessages.MISMATCH_RECORD.getErrorMessage());
        }

        return new ModelMapper().map(awardEntity, AwardDto.class);
    }

    @Override
    public AwardDto updateAward(String regNo, String awardId, AwardDto awardDto) {
        StudentEntity studentEntity = checkStudentEntity(regNo);

        AwardEntity awardEntity = checkAwardEntity(awardId);

        if (studentEntity != awardEntity.getStudentEntity()){
            throw new SchoolServiceException(ErrorMessages.MISMATCH_RECORD.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();

        awardEntity.setTitle(awardDto.getTitle());
        awardEntity.setYear(awardDto.getYear());

        AwardEntity updatedAward = awardRepository.save(awardEntity);

        return modelMapper.map(updatedAward, AwardDto.class);
    }

    @Override
    public List<AwardDto> getAwards(String regNo, int page, int limit) {
        List<AwardDto> returnValue = new ArrayList<>();

        StudentEntity studentEntity = checkStudentEntity(regNo);

        if (page > 0) page = page - 1;
        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<AwardEntity> pagedAwardEntities = awardRepository.findAllByStudentEntityOrderByYearDesc(studentEntity, pageableRequest);

        List<AwardEntity> entityList = pagedAwardEntities.getContent();
        for (AwardEntity awardEntity : entityList) {
            returnValue.add(new ModelMapper().map(awardEntity, AwardDto.class));
        }

        return returnValue;
    }

    @Override
    public void deleteAward(String regNo,String awardId) {
        StudentEntity studentEntity = checkStudentEntity(regNo);

        AwardEntity awardEntity = checkAwardEntity(awardId);

        if (studentEntity != awardEntity.getStudentEntity()){
            throw new SchoolServiceException(ErrorMessages.MISMATCH_RECORD.getErrorMessage());
        }

        awardRepository.delete(awardEntity);
    }

    private AwardEntity getAwardById(String awardId) {
        return awardRepository.findByAwardId(awardId);
    }

    private StudentEntity checkStudentEntity(String regNo) {
        return studentService.getStudentEntity(regNo);
    }

    private AwardEntity checkAwardEntity(String awardId) {
        AwardEntity awardEntity = getAwardById(awardId);
        if (awardEntity == null) {
            throw new SchoolServiceException(ErrorMessages.NO_AWARD_FOUND.getErrorMessage());
        }
        return awardEntity;
    }
}
