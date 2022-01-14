package com.example.sampleschool.io.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "roles")
public class RolesEntity implements Serializable {
    private static final long serialVersionUID = 357164337356504736L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<StudentEntity> student_tb;

    @ManyToMany(mappedBy = "roles")
    private Collection<TeacherEntity> teacher_tb;

    @ManyToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "roles_authorities",
            joinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authorities_id", referencedColumnName = "id"))
    private Collection<AuthorityEntity> authorities;

    public RolesEntity() {
    }

    public RolesEntity(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<StudentEntity> getStudent_tb() {
        return student_tb;
    }

    public void setStudent_tb(Collection<StudentEntity> student_tb) {
        this.student_tb = student_tb;
    }

    public Collection<AuthorityEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<AuthorityEntity> authorities) {
        this.authorities = authorities;
    }

    public Collection<TeacherEntity> getTeacher_tb() {
        return teacher_tb;
    }

    public void setTeacher_tb(Collection<TeacherEntity> teacher_tb) {
        this.teacher_tb = teacher_tb;
    }
}
