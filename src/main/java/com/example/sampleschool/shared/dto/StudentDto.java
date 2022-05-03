package com.example.sampleschool.shared.dto;


import com.example.sampleschool.ui.model.response.AwardRest;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class StudentDto implements Serializable {
    private static final long serialVersionUID = -3197131818142498182L;

    private long id;
    private String regNo;
    private String firstname;
    private String lastname;
    private String course;
    private String address;
    private String email;
    private String house;
    private String role;
    private String password;
    private List<String> subjectTeachers;
    private Timestamp timestamp;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private TeacherDto teacherDto;
    private List<AwardRest> awardRest;
    private List<AwardDto> awardDto;
    private Collection<String> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getSubjectTeachers() {
        return subjectTeachers;
    }

    public void setSubjectTeachers(List<String> subjectTeachers) {
        this.subjectTeachers = subjectTeachers;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Boolean getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
    }

    public TeacherDto getTeacherDto() {
        return teacherDto;
    }

    public void setTeacherDto(TeacherDto teacherDto) {
        this.teacherDto = teacherDto;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<AwardRest> getAwardRest() {
        return awardRest;
    }

    public void setAwardRest(List<AwardRest> awardRest) {
        this.awardRest = awardRest;
    }

    public List<AwardDto> getAwardDto() {
        return awardDto;
    }

    public void setAwardDto(List<AwardDto> awardDto) {
        this.awardDto = awardDto;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }
}
