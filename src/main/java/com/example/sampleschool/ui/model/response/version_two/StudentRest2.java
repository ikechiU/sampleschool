package com.example.sampleschool.ui.model.response.version_two;

import com.example.sampleschool.ui.model.response.AwardRest;

import java.sql.Timestamp;
import java.util.List;

public class StudentRest2 {
    private String regNo;
    private String firstname;
    private String lastname;
    private String course;
    private String address;
    private String email;
    private String house;
    private String role;
    private Timestamp timestamp;
    private List<String> subjectTeachers;
    private List<AwardRest> awardRest;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getSubjectTeachers() {
        return subjectTeachers;
    }

    public void setSubjectTeachers(List<String> subjectTeachers) {
        this.subjectTeachers = subjectTeachers;
    }

    public List<AwardRest> getAwardRest() {
        return awardRest;
    }

    public void setAwardRest(List<AwardRest> awardRest) {
        this.awardRest = awardRest;
    }
}
