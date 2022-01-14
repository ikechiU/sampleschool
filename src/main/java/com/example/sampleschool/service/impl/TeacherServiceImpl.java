package com.example.sampleschool.service.impl;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.io.entity.RolesEntity;
import com.example.sampleschool.io.entity.StudentEntity;
import com.example.sampleschool.io.entity.TeacherEntity;
import com.example.sampleschool.io.repository.AwardRepository;
import com.example.sampleschool.io.repository.RolesRepository;
import com.example.sampleschool.io.repository.StudentRepository;
import com.example.sampleschool.io.repository.TeacherRepository;
import com.example.sampleschool.security.UserPrinciple;
import com.example.sampleschool.service.TeacherService;
import com.example.sampleschool.shared.Role;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.shared.dto.TeacherDto;
import com.example.sampleschool.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    AwardRepository awardRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RolesRepository rolesRepository;

    @Override
    public TeacherDto createTeacher(TeacherDto teacherDto) {

        String emailName = "";
        String firstNameDotLastName = utils.getFirstNameDotLastName(teacherDto.getFirstname(), teacherDto.getLastname());

        if (teacherRepository.findTeacherEntitiesByEmail(firstNameDotLastName + "@school.com") == null) {
            emailName = firstNameDotLastName;
        } else {
            if (teacherRepository.findTeacherEntitiesByEmail(firstNameDotLastName + "_1@school.com") == null) {
                emailName = firstNameDotLastName + "_1";
            } else {
                if (teacherRepository.findTeacherEntitiesByEmail(firstNameDotLastName + "_2@school.com") == null) {
                    emailName = firstNameDotLastName + "_2";
                } else {
                    throw new SchoolServiceException(ErrorMessages.CONTACT_ADMIN_EMAIL_ISSUE.getErrorMessage());
                }
            }
        }

        if (teacherRepository.findTeacherEntitiesBySubject(utils.getUpperCaseText(teacherDto.getSubject())) != null) {
            throw new SchoolServiceException(ErrorMessages.SUBJECT_TEACHER_ALREADY_EXIST.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        TeacherEntity teacherEntity = modelMapper.map(teacherDto, TeacherEntity.class);

        String courseTeacher = utils.getSection(teacherDto.getSubject());

        String teacherId = utils.generateTeacherId(10);

        teacherEntity.setTeacherId(teacherId);
        teacherEntity.setFirstname(utils.getCapitalizeName(teacherDto.getFirstname()));
        teacherEntity.setLastname(utils.getCapitalizeName(teacherDto.getLastname()));
        teacherEntity.setSubject(utils.getUpperCaseText(teacherDto.getSubject()));
        teacherEntity.setEmail(emailName + "@school.com");
        teacherEntity.setRole(Role.ROLE_TEACHER.name());
        teacherEntity.setSection(courseTeacher);
        teacherEntity.setEncryptedPassword(teacherDto.getPassword());
        teacherEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(teacherDto.getPassword()));
        teacherEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(teacherId));

        // Set roles
        Collection<RolesEntity> roleEntities = new HashSet<>();
        RolesEntity roleEntity = rolesRepository.findByName(Role.ROLE_USER.name());
        if(roleEntity != null) {
            roleEntities.add(roleEntity);
        }
        roleEntities.add(roleEntity);

        teacherEntity.setRoles(roleEntities);

        TeacherEntity createdTeacher = teacherRepository.save(teacherEntity);

        TeacherDto returnValue = modelMapper.map(createdTeacher, TeacherDto.class);
        returnValue.setStudents(getStudents(courseTeacher));

        return returnValue;
    }

    @Override
    public TeacherDto getTeacherLoginDetails(String teacherId) { //Used by AuthenticationFilter to get response headers
        TeacherEntity teacherEntity = getTeacherEntity(teacherId);
        return new ModelMapper().map(teacherEntity, TeacherDto.class);
    }

    @Override
    public TeacherDto getTeacher(String teacherId) {
        TeacherEntity teacherEntity = getTeacherEntity(teacherId);

        TeacherDto returnValue = new ModelMapper().map(teacherEntity, TeacherDto.class);
        returnValue.setStudents(getStudents(teacherEntity.getSection()));

        return returnValue;
    }

    @Override
    public TeacherDto updateTeacher(String teacherId, TeacherDto teacherDto) {
        TeacherEntity teacherEntity = getTeacherEntity(teacherId);

        teacherEntity.setAddress(teacherDto.getAddress());

        TeacherEntity updatedTeacherEntity = teacherRepository.save(teacherEntity);

        TeacherDto returnValue = new ModelMapper().map(updatedTeacherEntity, TeacherDto.class);
        returnValue.setStudents(getStudents(teacherEntity.getSection()));

        return returnValue;
    }

    @Override
    public List<TeacherDto> getTeachers(int page, int limit) {
        List<TeacherDto> returnValue;

        if (page > 0)
            page = page - 1;

        Pageable pageableRequest = PageRequest.of(page, limit);
        Page<TeacherEntity> teacherEntities = teacherRepository.findAll(pageableRequest);

        List<TeacherEntity> entityList = teacherEntities.getContent();

        Type listType = new TypeToken<List<TeacherDto>>() {}.getType();
        returnValue = new ModelMapper().map(entityList, listType);

        for(int i= 0; i < entityList.size(); i++){
            returnValue.get(i).setStudents(getStudents(entityList.get(i).getSection()));
            returnValue.set(i, returnValue.get(i));
        }

        return returnValue;
    }

    @Override
    public void deleteTeacher(String teacherId) {
        TeacherEntity teacherEntity = getTeacherEntity(teacherId);

        teacherRepository.delete(teacherEntity);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TeacherEntity teacherEntity = new TeacherEntity();
        String userName = "";

        if (username.contains("@")) {
            teacherEntity = teacherRepository.findTeacherEntitiesByEmail(username);

            if (teacherEntity == null)
                throw new UsernameNotFoundException("No user with email: " + username);
            userName = teacherEntity.getEmail();
        } else {
            teacherEntity = teacherRepository.findTeacherEntitiesByTeacherId(username);

            if (teacherEntity == null)
                throw new UsernameNotFoundException("No user with id: " + username);
            userName = teacherEntity.getTeacherId();
        }

//        return new User(userName, teacherEntity.getEncryptedPassword(), teacherEntity.getEmailVerificationStatus(),
//                true, true, true, new ArrayList<>());

        return new UserPrinciple(new StudentEntity(), teacherEntity, userName);
    }


    private List<String> getStudents(String courseTeacher) {
        ArrayList<String> list = new ArrayList<>();

        if (courseTeacher.equals("General course teacher")) {
            list.addAll(artStudents());
            list.addAll(scienceStudents());
        }

        if (courseTeacher.equals("Science course teacher")) {
            list = scienceStudents();
        }

        if (courseTeacher.equals("Art course teacher")) {
            list = artStudents();
        }

        return list;
    }

    private ArrayList<String> artStudents() {
        ArrayList<String> list = new ArrayList<>();
        List<StudentEntity> entityList = studentRepository.findAllByCourse("ART");
        for (StudentEntity studentEntity : entityList) {
            String firstname = studentEntity.getFirstname();
            String lastname = studentEntity.getLastname();

            String name = firstname + " " + lastname;
            list.add(name);
        }
        return list;
    }

    private ArrayList<String> scienceStudents() {
        ArrayList<String> list = new ArrayList<>();
        List<StudentEntity> entityList = studentRepository.findAllByCourse("SCIENCE");
        for (StudentEntity studentEntity : entityList) {
            String firstname = studentEntity.getFirstname();
            String lastname = studentEntity.getLastname();

            String name = firstname + " " + lastname;
            list.add(name);
        }
        return list;
    }

    private TeacherEntity getTeacherEntity(String query) {
        TeacherEntity teacherEntity = new TeacherEntity();
        if (query.contains("@")) {
            teacherEntity = teacherRepository.findTeacherEntitiesByEmail(query);
        } else {
            teacherEntity = teacherRepository.findTeacherEntitiesByTeacherId(query);
        }

        if(teacherEntity == null) {
            throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        return teacherEntity;
    }

}
