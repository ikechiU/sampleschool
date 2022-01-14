package com.example.sampleschool;

import com.example.sampleschool.io.entity.AuthorityEntity;
import com.example.sampleschool.io.entity.RolesEntity;
import com.example.sampleschool.io.entity.TeacherEntity;
import com.example.sampleschool.io.repository.AuthorityRepository;
import com.example.sampleschool.io.repository.RolesRepository;
import com.example.sampleschool.io.repository.StudentRepository;
import com.example.sampleschool.io.repository.TeacherRepository;
import com.example.sampleschool.shared.Role;
import com.example.sampleschool.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Component
public class InitialUsersSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    Utils utils;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("From Application ready event...");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity superWriteAuthority = createAuthority("SUPER_WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        createRole(Role.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        RolesEntity roleSuperWrite = createRole(Role.ROLE_SUPER_WRITE.name(), Arrays.asList(readAuthority, writeAuthority, superWriteAuthority));
        RolesEntity roleAdmin = createRole(Role.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, superWriteAuthority,  deleteAuthority));

        if(roleAdmin == null) return;

        TeacherEntity adminUser = new TeacherEntity();
        adminUser.setTeacherId(utils.generateTeacherId(15));
        adminUser.setFirstname("Ikechi");
        adminUser.setLastname("Ucheagwu");
        adminUser.setSubject("All Subject");
        adminUser.setAddress("Obajana Kogi");
        adminUser.setEmail("admin@school.com");
        adminUser.setRole(Role.ROLE_ADMIN.name());
        adminUser.setSection(Role.ROLE_ADMIN.name());
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("QzWbYapLt")); //Dont try to guess the admin password
        adminUser.setEmailVerificationStatus(true);
        adminUser.setRoles(Collections.singletonList(roleAdmin));

        TeacherEntity teacherEntity = teacherRepository.findTeacherEntitiesByEmail("admin@school.com");
        if (teacherEntity == null) {
            teacherRepository.save(adminUser);
        }


        if(roleSuperWrite == null) return;

        TeacherEntity superWriteUser = new TeacherEntity();
        superWriteUser.setTeacherId(utils.generateTeacherId(15));
        superWriteUser.setFirstname("Henry");
        superWriteUser.setLastname("Smith");
        superWriteUser.setSubject("All Subject");
        superWriteUser.setAddress("Ikotun Lagos");
        superWriteUser.setEmail("super.write@school.com");
        superWriteUser.setRole(Role.ROLE_SUPER_WRITE.name());
        superWriteUser.setSection(Role.ROLE_SUPER_WRITE.name());
        superWriteUser.setEncryptedPassword(bCryptPasswordEncoder.encode("123456789"));
        superWriteUser.setEmailVerificationStatus(true);
        superWriteUser.setRoles(Collections.singletonList(roleSuperWrite));

        TeacherEntity teacherEntity1 = teacherRepository.findTeacherEntitiesByEmail("super.write@school.com");
        if (teacherEntity1 == null) {
            teacherRepository.save(superWriteUser);
        }

    }

    @Transactional
    protected AuthorityEntity createAuthority(String name) {

        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    @Transactional
    protected RolesEntity createRole(
            String name, Collection<AuthorityEntity> authorities) {

        RolesEntity role = rolesRepository.findByName(name);
        if (role == null) {
            role = new RolesEntity(name);
            role.setAuthorities(authorities);
            rolesRepository.save(role);
        }
        return role;
    }

}
