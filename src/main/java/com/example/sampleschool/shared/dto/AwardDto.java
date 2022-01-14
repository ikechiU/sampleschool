package com.example.sampleschool.shared.dto;

import java.io.Serializable;

public class AwardDto implements Serializable {
    private static final long serialVersionUID = -3197133818142498182L;

    private long id;
    private String awardId;
    private String title;
    private String year;
    private StudentDto studentDto;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAwardId() {
        return awardId;
    }

    public void setAwardId(String awardId) {
        this.awardId = awardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public StudentDto getStudentDto() {
        return studentDto;
    }

    public void setStudentDto(StudentDto studentDto) {
        this.studentDto = studentDto;
    }
}
