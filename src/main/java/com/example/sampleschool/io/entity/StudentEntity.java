package com.example.sampleschool.io.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "student_tb")
public class StudentEntity implements Serializable {

    private static final long serialVersionUID = -23714379761985116L;

    @Id @GeneratedValue private Long id;
    @Column(length = 30) private String regNo;
    @Column(length = 30) private String firstname;
    @Column(length = 30) private String lastname;
    @Column(length = 30) private String course;
    @Column(length = 30) private String address;
    @Column(length = 150) private String email;
    @Column(length = 30) private String house;
    @Column(length = 30) private String role;
    @Column(nullable = false) private String encryptedPassword;
    private String emailVerificationToken;
    @Column(nullable = false) private Boolean emailVerificationStatus = false;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS, timezone = UTC")
    @CreationTimestamp
    @Column private Timestamp timestamp;
    @OneToMany(mappedBy = "studentEntity", cascade = CascadeType.ALL) private List<AwardEntity> awardEntity;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "student_tb_roles",
            joinColumns = @JoinColumn (name = "student_tb_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"))
    private Collection<RolesEntity> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<AwardEntity> getAwardEntity() {
        return awardEntity;
    }

    public void setAwardEntity(List<AwardEntity> awardEntity) {
        this.awardEntity = awardEntity;
    }

    public Collection<RolesEntity> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RolesEntity> roles) {
        this.roles = roles;
    }
}
