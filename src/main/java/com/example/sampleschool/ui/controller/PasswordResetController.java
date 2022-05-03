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
public class PasswordResetController {

    @Autowired
    StudentService studentService;

    @Operation(summary = "PASSWORD RESET REQUEST. To change your password you will need to supply the email used in creating the account.")
    @PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = studentService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    @Operation(summary = "PASSWORD RESET. Supply the token sent to you via email and your new password to reset you password.")
    @PostMapping(path = "/password-reset",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = studentService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

}
