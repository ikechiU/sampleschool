package com.example.sampleschool.service.impl;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.io.entity.*;
import com.example.sampleschool.io.repository.*;
import com.example.sampleschool.security.UserPrinciple;
import com.example.sampleschool.service.StudentService;
import com.example.sampleschool.shared.AmazonSES;
import com.example.sampleschool.shared.Role;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.ui.model.response.AwardRest;
import com.example.sampleschool.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

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
    AmazonSES amazonSES;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public StudentDto createStudent(StudentDto studentDto) {

        if (studentRepository.findStudentEntitiesByEmail(studentDto.getEmail()) != null) {
            throw new SchoolServiceException(ErrorMessages.EMAIL_ALREADY_EXISTS.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        StudentEntity studentEntity = modelMapper.map(studentDto, StudentEntity.class);

        String regNo = utils.generateStudentRegNo(10);

        studentEntity.setRegNo(regNo);
        studentEntity.setFirstname(utils.getCapitalizeName(studentDto.getFirstname()));
        studentEntity.setLastname(utils.getCapitalizeName(studentDto.getLastname()));
        studentEntity.setCourse(utils.getUpperCaseText(studentDto.getCourse()));
        studentEntity.setHouse(utils.getHouse());
        studentEntity.setRole(Role.ROLE_STUDENT.name());
        studentEntity.setEncryptedPassword(studentDto.getPassword());
        studentEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(studentDto.getPassword()));
        studentEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(regNo));
        studentEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // Set roles
        Collection<RolesEntity> roleEntities = new HashSet<>();
        RolesEntity roleEntity = rolesRepository.findByName(Role.ROLE_USER.name());
        if(roleEntity != null) {
            roleEntities.add(roleEntity);
        }
        roleEntities.add(roleEntity);

        studentEntity.setRoles(roleEntities);

        StudentEntity createdStudent = studentRepository.save(studentEntity);

        StudentDto returnValue = modelMapper.map(createdStudent, StudentDto.class);
        returnValue.setSubjectTeachers(getSubjectTeachers(createdStudent.getCourse()));

        amazonSES.verifyEmail(returnValue); // Send an email message to user to verify their email address

        return returnValue;
    }

    @Override
    public StudentDto getStudentLoginDetails(String regNo) { //Used by AuthenticationFilter to get response headers
        StudentEntity studentEntity = getStudentEntity(regNo);
        return new ModelMapper().map(studentEntity, StudentDto.class);
    }

    @Override
    public StudentDto getStudent(String regNo) {
        StudentEntity studentEntity = getStudentEntity(regNo);

        StudentDto returnValue = new ModelMapper().map(studentEntity, StudentDto.class);
        returnValue.setSubjectTeachers(getSubjectTeachers(studentEntity.getCourse()));
        returnValue.setAwardRest(getAwardRestList(studentEntity.getAwardEntity()));

        return returnValue;
    }

    @Override
    public StudentDto updateStudent(String regNo, StudentDto studentDto) {
        StudentEntity studentEntity = getStudentEntity(regNo);

        studentEntity.setAddress(studentDto.getAddress());

        StudentEntity updatedStudentEntity = studentRepository.save(studentEntity);

        StudentDto returnValue = new ModelMapper().map(updatedStudentEntity, StudentDto.class);
        returnValue.setSubjectTeachers(getSubjectTeachers(studentEntity.getCourse()));
        returnValue.setAwardRest(getAwardRestList(studentEntity.getAwardEntity()));

        return returnValue;
    }

    @Override
    public List<StudentDto> getStudents(int page, int limit, String course) {

        List<StudentDto> returnValue;

        List<StudentEntity> entityList;

        if (page > 0) page = page - 1;
        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by("timestamp").descending());
        Page<StudentEntity> studentEntities;

        if (course.isEmpty()) {
            studentEntities = studentRepository.findAll(pageableRequest);
        } else {
            studentEntities = studentRepository.findAllByCourseContaining(course, pageableRequest);
        }

        entityList = studentEntities.getContent();
        returnValue =  getStudentDtos(entityList);

        for (int i = 0; i < entityList.size(); i++) {
            returnValue.get(i).setSubjectTeachers(getSubjectTeachers(entityList.get(i).getCourse()));
            returnValue.set(i, returnValue.get(i));
        }

        for (int i = 0; i < entityList.size(); i++) {
            returnValue.get(i).setAwardRest(getAwardRestList(entityList.get(i).getAwardEntity()));
            returnValue.set(i, returnValue.get(i));
        }

        return returnValue;
    }

    private List<StudentDto> getStudentDtos(List<StudentEntity> entityList) {
        Type listType = new TypeToken<List<StudentDto>>() {}.getType();
        return new ModelMapper().map(entityList, listType);
    }

    @Override
    public void deleteStudent(String regNo) {
        StudentEntity studentEntity = getStudentEntity(regNo);

        studentRepository.delete(studentEntity);
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        StudentEntity studentEntity = studentRepository.findStudentEntityByEmailVerificationToken(token);

        if (studentEntity != null) {
            boolean hastokenExpired = Utils.hasTokenExpired(token);
            if (!hastokenExpired) {
                studentEntity.setEmailVerificationToken(null);
                studentEntity.setEmailVerificationStatus(Boolean.TRUE);
                studentRepository.save(studentEntity);
                returnValue = true;
            }
        }

        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;

        StudentEntity userEntity = studentRepository.findStudentEntitiesByEmail(email);

        if (userEntity == null) {
            return returnValue;
        }

        String token = new Utils().generatePasswordResetToken(userEntity.getRegNo());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setStudentEntity(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        returnValue = new AmazonSES().sendPasswordResetRequest(
                userEntity.getFirstname(),
                userEntity.getEmail(),
                token);

        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;

        if (Utils.hasTokenExpired(token)) {
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return returnValue;
        }

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // Update User password in database
        StudentEntity userEntity = passwordResetTokenEntity.getStudentEntity();
        userEntity.setEncryptedPassword(encodedPassword);
        StudentEntity savedStudentEntity = studentRepository.save(userEntity);

        // Verify if password was saved successfully
        if (savedStudentEntity != null && savedStudentEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }

        // Remove Password Reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StudentEntity studentEntity = new StudentEntity();
        String userName = "";

        if (username.contains("@")) {
            studentEntity = studentRepository.findStudentEntitiesByEmail(username);

            if (studentEntity == null)
                throw new UsernameNotFoundException("No user with email: " + username);
            userName = studentEntity.getEmail();
        } else {
            studentEntity = studentRepository.findStudentEntitiesByRegNo(username);

            if (studentEntity == null)
                throw new UsernameNotFoundException("No user with id: " + username);
            userName = studentEntity.getRegNo();
        }

//        return new User(userName, studentEntity.getEncryptedPassword(), new ArrayList<>());
//        return new User(userName, studentEntity.getEncryptedPassword(), studentEntity.getEmailVerificationStatus(),
//                true, true, true, new ArrayList<>());
        return new UserPrinciple(studentEntity, new TeacherEntity(), userName);
    }


    private List<String> getSubjectTeachers(String course) {
        ArrayList<String> list = new ArrayList<>();
        TeacherEntity teacherEntity = new TeacherEntity();

        teacherEntity = teacherRepository.findTeacherEntitiesBySubject("MATHS");
        String mathsTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

        teacherEntity = teacherRepository.findTeacherEntitiesBySubject("ENGLISH");
        String englishTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

        teacherEntity = teacherRepository.findTeacherEntitiesBySubject("ENTREPRENEUR");
        String entrepreneurTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

        list.add("Maths: " + mathsTeacher);
        list.add("English: " + englishTeacher);
        list.add("Entrepreneur: " + entrepreneurTeacher);

        if (course.equals("SCIENCE")) {

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("PHYSICS");
            String physicsTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("CHEMISTRY");
            String chemistryTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("COMPUTER");
            String computerTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("BIOLOGY");
            String biologyTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            list.add("Physics: " + physicsTeacher);
            list.add("Chemistry: " + chemistryTeacher);
            list.add("Computer: " + computerTeacher);
            list.add("Biology: " + biologyTeacher);

        } else if (course.equals("ART")) {
            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("FINE ARTS");
            String fineArtsTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("GOVERNMENT");
            String governmentTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("ECONOMICS");
            String economicsTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            teacherEntity = teacherRepository.findTeacherEntitiesBySubject("COMMERCE");
            String commerceTeacher = teacherEntity == null ? "-" : teacherEntity.getFirstname() + " " + teacherEntity.getLastname();

            list.add("Fine arts: " + fineArtsTeacher);
            list.add("Government: " + governmentTeacher);
            list.add("Economics: " + economicsTeacher);
            list.add("Commerce: " + commerceTeacher);
        }

        return list;
    }

    public StudentEntity getStudentEntity(String query) {
        StudentEntity studentEntity = new StudentEntity();
        if (query.contains("@")) {
            studentEntity = studentRepository.findStudentEntitiesByEmail(query);
        } else {
            studentEntity = studentRepository.findStudentEntitiesByRegNo(query);
        }

        if (studentEntity == null) {
            throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        return studentEntity;
    }

    @Transactional
    public List<AwardRest> getAwardRestList(List<AwardEntity> awardEntities) {
        List<AwardRest> awardRestList = new ArrayList<>();

        for (AwardEntity awardEntity : awardEntities) {
            AwardRest awardRest = new AwardRest();
            awardRest.setAwardId(awardEntity.getAwardId());
            awardRest.setTitle(awardEntity.getTitle());
            awardRest.setYear(awardEntity.getYear());

            awardRestList.add(awardRest);
        }

        return awardRestList;
    }


}
