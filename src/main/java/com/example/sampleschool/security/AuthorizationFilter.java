package com.example.sampleschool.security;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.io.entity.StudentEntity;
import com.example.sampleschool.io.entity.TeacherEntity;
import com.example.sampleschool.io.repository.StudentRepository;
import com.example.sampleschool.io.repository.TeacherRepository;
import com.example.sampleschool.ui.model.response.ErrorMessages;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public AuthorizationFilter(AuthenticationManager authManager, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        super(authManager);
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {

            token = token.replace(SecurityConstants.TOKEN_PREFIX, ""); //remove "Bearer " we only need token secret

            String user = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            StudentEntity studentEntity = new StudentEntity();
            TeacherEntity teacherEntity = new TeacherEntity();

            if (user != null) {

                if (user.contains("@school.com")){
                    teacherEntity = teacherRepository.findTeacherEntitiesByEmail(user);
                    if(teacherEntity == null)
                        throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " email.");
                } else if (user.contains("T")) {
                    teacherEntity = teacherRepository.findTeacherEntitiesByTeacherId(user);
                    if(teacherEntity == null)
                        throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " TeacherId.");
                } else if (user.contains("S")) {
                    studentEntity = studentRepository.findStudentEntitiesByRegNo(user);
                    if(studentEntity == null)
                        throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " regNo.");
                } else {
                    studentEntity = studentRepository.findStudentEntitiesByEmail(user);
                    if(studentEntity == null)
                        throw new SchoolServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " email.");
                }

                //return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

                UserPrinciple userPrinciple = new UserPrinciple(studentEntity, teacherEntity, user);
                return new UsernamePasswordAuthenticationToken(userPrinciple, null, userPrinciple.getAuthorities());
            }

            return null;
        }

        return null;
    }

}
