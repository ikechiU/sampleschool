package com.example.sampleschool.security;

import com.example.sampleschool.SpringApplicationContext;
import com.example.sampleschool.service.StudentService;
import com.example.sampleschool.service.TeacherService;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.shared.dto.TeacherDto;
import com.example.sampleschool.ui.model.request.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            LoginRequestModel credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);

            String username = "";
            if (credentials.getEmail() == null) {
                username = credentials.getId();
            }
            if (credentials.getId() == null ) {
                username = credentials.getEmail();
            }

            //look up user in our database
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, credentials.getPassword(), new ArrayList<>())
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //once request is successful
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

//        String username = ((User) authResult.getPrincipal()).getUsername();
        String username = ((UserPrinciple) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        StudentService studentService = (StudentService) SpringApplicationContext.getBean("studentServiceImpl");
        TeacherService teacherService = (TeacherService) SpringApplicationContext.getBean("teacherServiceImpl");

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

        if (username.contains("T") || username.contains("@school.com")){
            TeacherDto teacherDto = teacherService.getTeacherLoginDetails(username);

            response.addHeader(SecurityConstants.TEACHER_ID, teacherDto.getTeacherId());
            response.addHeader(SecurityConstants.LOGIN_ROLE, teacherDto.getRole());
            response.addHeader(SecurityConstants.FIRST_NAME, teacherDto.getFirstname());
            response.addHeader(SecurityConstants.LAST_NAME, teacherDto.getLastname());
        } else {
            StudentDto studentDto = studentService.getStudentLoginDetails(username);

            response.addHeader(SecurityConstants.REG_NO, studentDto.getRegNo());
            response.addHeader(SecurityConstants.LOGIN_ROLE, studentDto.getRole());
            response.addHeader(SecurityConstants.FIRST_NAME, studentDto.getFirstname());
            response.addHeader(SecurityConstants.LAST_NAME, studentDto.getLastname());
        }

    }

}