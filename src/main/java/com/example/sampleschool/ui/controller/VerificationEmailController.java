package com.example.sampleschool.ui.controller;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.service.AwardService;
import com.example.sampleschool.service.StudentService;
import com.example.sampleschool.service.TeacherService;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.AwardDto;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.ui.model.request.*;
import com.example.sampleschool.ui.model.response.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sample-school")
public class VerificationEmailController {

    @Autowired
    StudentService studentService;

    @Operation(summary = "VERIFY EMAIL. Supply the token sent to your email upon account registration to verify your email.")
    @GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = studentService.verifyEmailToken(token);

        if(isVerified)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

}
