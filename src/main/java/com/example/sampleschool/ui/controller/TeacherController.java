package com.example.sampleschool.ui.controller;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.service.AwardService;
import com.example.sampleschool.service.StudentService;
import com.example.sampleschool.service.TeacherService;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.shared.dto.TeacherDto;
import com.example.sampleschool.ui.model.request.TeacherRequest;
import com.example.sampleschool.ui.model.request.UpdateRequest;
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
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/sample-school")
public class TeacherController {

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    AwardService awardService;

    @Autowired Utils utils;


    @Operation(summary = "HTTP POST Web Service Endpoint to create a teacher profile it returns a TeacherRest response.")
    @PostMapping(path = "/teacher", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TeacherRest createTeacher(@RequestBody TeacherRequest teacherRequest) {
        TeacherRest returnValue = new TeacherRest();

        if (teacherRequest.getFirstname().isEmpty() || teacherRequest.getLastname().isEmpty() ||
                teacherRequest.getAddress().isEmpty() || teacherRequest.getSubject().isEmpty() ||
                teacherRequest.getPassword().isEmpty()) {
            throw new SchoolServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        if (teacherRequest.getFirstname().length() < 2 || teacherRequest.getLastname().length() < 2) {
            throw new SchoolServiceException(ErrorMessages.NAME_LENGTH_SHORT.getErrorMessage());
        }

        if (!utils.isSubject(utils.getUpperCaseText(teacherRequest.getSubject()))){
            throw new SchoolServiceException(ErrorMessages.SUBJECT_TYPE_ERROR.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        TeacherDto teacherDto = modelMapper.map(teacherRequest, TeacherDto.class);

        TeacherDto teacher = teacherService.createTeacher(teacherDto);
        returnValue = modelMapper.map(teacher, TeacherRest.class);

        return returnValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${teacherController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a single teacher detail (TeacherRest)", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${teacherController.GetTeacher.ApiOperation.Notes}" )
    @GetMapping(path = "/teacher/{teacherId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TeacherRest getTeacher(@PathVariable String teacherId) {

        TeacherDto teacher = teacherService.getTeacher(teacherId);

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(teacher, TeacherRest.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${teacherController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP PUT Web Service Endpoint to update a single teacher detail.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${teacherController.GetTeacher.ApiOperation.Notes}" )
    @PutMapping(path = "/teacher/{teacherId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TeacherRest updateTeacher(@PathVariable String teacherId, @RequestBody UpdateRequest updateRequest) {

        TeacherDto teacherDto = new TeacherDto();
        teacherDto = new ModelMapper().map(updateRequest, TeacherDto.class);

        TeacherDto teacher = teacherService.updateTeacher(teacherId, teacherDto);

        return new ModelMapper().map(teacher, TeacherRest.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${teacherController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP GET Web Service Endpoint to get a list of teachers List<TeacherRest>", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${teacherController.GetTeacher.ApiOperation.Notes}" )
    @GetMapping(path = "/teacher", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<TeacherRest> getTeachers(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<TeacherRest> returnValue;

        List<TeacherDto> teachers = teacherService.getTeachers(page, limit);

        Type listType = new TypeToken<List<TeacherRest>>() {}.getType();
        returnValue = new ModelMapper().map(teachers, listType);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${teacherController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "HTTP DELETE Web Service Endpoint to delete a teacher detail. Only an admin perform this action", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${teacherController.DeleteStudent.ApiOperation.Notes}" )
    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "/teacher/{teacherId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteTeacher(@PathVariable String teacherId) {
        OperationStatusModel returnValue = new OperationStatusModel();

        teacherService.deleteTeacher(teacherId);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

}
