package com.example.sampleschool.security;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.io.entity.AuthorityEntity;
import com.example.sampleschool.io.entity.RolesEntity;
import com.example.sampleschool.io.entity.StudentEntity;
import com.example.sampleschool.io.entity.TeacherEntity;
import com.example.sampleschool.ui.model.response.ErrorMessages;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserPrinciple implements UserDetails {

    private final StudentEntity studentEntity;
    private final TeacherEntity teacherEntity;
    private final String userName;
    private String regNo;
    private String teacherId;

    public UserPrinciple(StudentEntity studentEntity, TeacherEntity teacherEntity, String userName) {
        this.studentEntity = studentEntity;
        this.teacherEntity = teacherEntity;
        this.userName = userName;
        regNo = studentEntity.getRegNo();
        teacherId = teacherEntity.getTeacherId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        Collection<RolesEntity> roles = studentEntity.getRoles();
        Collection<RolesEntity> roles2 = teacherEntity.getRoles();

        Collection<AuthorityEntity> authorityEntities = new HashSet<>();


        if (roles != null) {
            roles.forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
                authorityEntities.addAll(role.getAuthorities());
            });
        }

        if (roles2 != null) {
            roles2.forEach(role2 -> {
                authorities.add(new SimpleGrantedAuthority(role2.getName()));
                authorityEntities.addAll(role2.getAuthorities());
            });
        }

        authorityEntities.forEach(authorityEntity -> {
            authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        String returnValue;
        if (userName.contains("@school.com") || userName.contains("T")) {
            returnValue = teacherEntity.getEncryptedPassword();
        } else {
            returnValue = studentEntity.getEncryptedPassword();
        }
        return returnValue;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        boolean returnValue;
        if (userName.contains("@school.com") || userName.contains("T")) {
            returnValue = teacherEntity.getEmailVerificationStatus();
        } else {
            returnValue = studentEntity.getEmailVerificationStatus();
        }
        return returnValue;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
