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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sample-school")
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    AwardService awardService;

    @Autowired
    Utils utils;


    @Operation(summary = "HTTP POST Web Service Endpoint to create a student profile it returns a StudentRest.")
    @PostMapping(path = "/student", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest createStudent(@RequestBody StudentRequest studentRequest) {
        StudentRest returnValue = new StudentRest();

        if (studentRequest.getFirstname().isEmpty() || studentRequest.getLastname().isEmpty() ||
                studentRequest.getCourse().isEmpty() || studentRequest.getAddress().isEmpty() ||
                studentRequest.getEmail().isEmpty() || studentRequest.getPassword().isEmpty()) {
            throw new SchoolServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        if (studentRequest.getFirstname().trim().length() < 2 || studentRequest.getLastname().length() < 2) {
            throw new SchoolServiceException(ErrorMessages.NAME_LENGTH_SHORT.getErrorMessage());
        }

        if (!utils.isScienceOrArt(utils.getUpperCaseText(studentRequest.getCourse()))) {
            throw new SchoolServiceException(ErrorMessages.COURSE_TYPE_ERROR.getErrorMessage());
        }

        if (!utils.isEmailValid(studentRequest.getEmail())) {
            throw new SchoolServiceException(ErrorMessages.INVALID_EMAIL.getErrorMessage());
        }

        if (studentRequest.getEmail().contains("@school.com")) {
            throw new SchoolServiceException(ErrorMessages.RESERVED_EMAIL.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        StudentDto studentDto = modelMapper.map(studentRequest, StudentDto.class);

        StudentDto student = studentService.createStudent(studentDto);
        returnValue = modelMapper.map(student, StudentRest.class);

        return returnValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a single student detail (StudentRest)", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @GetMapping(path = "/student/{regNo}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest getStudent(@PathVariable String regNo) {

        StudentDto student = studentService.getStudent(regNo);

        return new ModelMapper().map(student, StudentRest.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP PUT Web Service Endpoint to update a single student detail returns StudentRest", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @PutMapping(path = "/student/{regNo}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest updateStudent(@PathVariable String regNo, @RequestBody UpdateRequest updateRequest) {

        StudentDto studentDto = new StudentDto();
        studentDto = new ModelMapper().map(updateRequest, StudentDto.class);

        StudentDto student = studentService.updateStudent(regNo, studentDto);

        return new ModelMapper().map(student, StudentRest.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a list of student details List<StudentRest>", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @GetMapping(path = "/student", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<StudentRest> getStudents(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit,
                                         @RequestParam(value = "course", defaultValue = "") String course) {

        if (!course.trim().isEmpty()){
            course = utils.getUpperCaseText(course.trim());
        }

        List<StudentRest> returnValue;

        List<StudentDto> students = studentService.getStudents(page, limit, course);

        Type listType = new TypeToken<List<StudentRest>>() {}.getType();
        returnValue = new ModelMapper().map(students, listType);

        return returnValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP DELETE Web Service Endpoint to delete a student detail. Only an admin or a user that created the account can delete it", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.DeleteStudent.ApiOperation.Notes}" )
    @PreAuthorize("hasRole('ADMIN') or #regNo == principal.regNo")
    @DeleteMapping(path = "/student/{regNo}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteStudent(@PathVariable String regNo) {
        OperationStatusModel returnValue = new OperationStatusModel();

        studentService.deleteStudent(regNo);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    //AWARDS
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP POST Web Service Endpoint to add an award for a student.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasAuthority('SUPER_WRITE_AUTHORITY')")
    @PostMapping(path = "/student/{regNo}/awards", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AwardRest addAward(@PathVariable String regNo, @RequestBody AwardRequest awardRequest) throws Exception {
        checkAwardRequest(awardRequest);
        ModelMapper modelMapper = new ModelMapper();

        AwardDto awardDto = modelMapper.map(awardRequest, AwardDto.class);

        AwardDto createdAward = awardService.createAward(regNo, awardDto);

        return modelMapper.map(createdAward, AwardRest.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a specific award for a student.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GeneralOne.ApiOperation.Notes}" )
    @GetMapping(path = "/student/{regNo}/awards/{awardId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AwardRest getAward(@PathVariable String regNo, @PathVariable String awardId) {

        AwardDto createdAward = awardService.getAward(regNo, awardId);

        return new ModelMapper().map(createdAward, AwardRest.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP PUT Web Service Endpoint to update an award for a student.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasAuthority('SUPER_WRITE_AUTHORITY')")
    @PutMapping(path = "/student/{regNo}/awards/{awardId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AwardRest updateAward(@PathVariable String regNo, @PathVariable String awardId, @RequestBody AwardRequest awardRequest) throws Exception {
        checkAwardRequest(awardRequest);
        ModelMapper modelMapper = new ModelMapper();

        AwardDto awardDto = modelMapper.map(awardRequest, AwardDto.class);

        AwardDto createdAward = awardService.updateAward(regNo, awardId, awardDto);

        return modelMapper.map(createdAward, AwardRest.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a list of awards for a student.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GeneralOne.ApiOperation.Notes}" )
    @GetMapping(path = "/student/{regNo}/awards", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AwardRest> getAwards(@PathVariable String regNo, @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<AwardRest> returnValue = new ArrayList<>();

        List<AwardDto> awards = awardService.getAwards(regNo, page, limit);

        if (awards != null && !awards.isEmpty()) {
            Type listType = new TypeToken<List<AwardRest>>() {}.getType();
            returnValue = new ModelMapper().map(awards, listType);
        }

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP DELETE Web Service Endpoint to delete an award for a student.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_WRITE')")
    @DeleteMapping(path = "/student/{regNo}/awards/{awardId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteAward(@PathVariable String regNo, @PathVariable String awardId) {
        OperationStatusModel returnValue = new OperationStatusModel();

        awardService.deleteAward(regNo, awardId);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @Operation(summary = "HTTP GET Web Service Endpoint.")
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

    @Operation(summary = "HTTP POST Web Service Endpoint to request password change. You will need to supply the email used in creating the account.")
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

    @Operation(summary = "HTTP POST Web Service Endpoint to change password after receiving token in your mail. You will need to supply the token and the new password to be updated for your account.")
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


    public void checkAwardRequest(AwardRequest awardRequest) {
        if (awardRequest.getTitle().isEmpty() || awardRequest.getYear().isEmpty()){
            throw new SchoolServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }
    }

}
