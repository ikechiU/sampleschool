package com.example.sampleschool.io.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "award_tb")
public class AwardEntity implements Serializable {
    private static final long serialVersionUID = -4197133818142498182L;

    @Id @GeneratedValue private long id;
    @Column(length = 30) private String awardId;
    private String title;
    @Column(length = 30) private String year;
    @ManyToOne @JoinColumn(name = "student_tb_id")private StudentEntity studentEntity;

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

    public StudentEntity getStudentEntity() {
        return studentEntity;
    }

    public void setStudentEntity(StudentEntity studentEntity) {
        this.studentEntity = studentEntity;
    }
}
