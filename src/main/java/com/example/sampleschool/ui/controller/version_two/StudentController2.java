package com.example.sampleschool.ui.controller.version_two;

import com.example.sampleschool.exceptions.SchoolServiceException;
import com.example.sampleschool.service.AwardService;
import com.example.sampleschool.service.StudentService;
import com.example.sampleschool.service.TeacherService;
import com.example.sampleschool.shared.Utils;
import com.example.sampleschool.shared.dto.AwardDto;
import com.example.sampleschool.shared.dto.StudentDto;
import com.example.sampleschool.ui.model.request.AwardRequest;
import com.example.sampleschool.ui.model.request.StudentRequest;
import com.example.sampleschool.ui.model.request.UpdateRequest;
import com.example.sampleschool.ui.model.response.*;
import com.example.sampleschool.ui.model.response.version_two.StudentRest2;
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
@RequestMapping("v2/sample-school")
public class StudentController2 {

    @Autowired
    StudentService studentService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    AwardService awardService;

    @Autowired
    Utils utils;


    @Operation(summary = "CREATE A STUDENT.")
    @PostMapping(path = "/students", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest2 createStudent(@RequestBody StudentRequest studentRequest) {
        StudentRest2 returnValue = new StudentRest2();

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
        returnValue = modelMapper.map(student, StudentRest2.class);

        return returnValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "GET A STUDENT. Supply student registration number. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @GetMapping(path = "/students/{regNo}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest2 getStudent(@PathVariable String regNo) {

        StudentDto student = studentService.getStudent(regNo);

        return new ModelMapper().map(student, StudentRest2.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "UPDATE A STUDENT. Supply student registration number and student details to be updated. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @PutMapping(path = "/students/{regNo}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public StudentRest2 updateStudent(@PathVariable String regNo, @RequestBody UpdateRequest updateRequest) {

        StudentDto studentDto = new StudentDto();
        studentDto = new ModelMapper().map(updateRequest, StudentDto.class);

        StudentDto student = studentService.updateStudent(regNo, studentDto);

        return new ModelMapper().map(student, StudentRest2.class);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "GET LIST OF STUDENTS. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GetStudent.ApiOperation.Notes}" )
    @GetMapping(path = "/students", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<StudentRest2> getStudents(@RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "limit", defaultValue = "10") int limit,
                                          @RequestParam(value = "course", defaultValue = "") String course) {

        if (!course.trim().isEmpty()){
            course = utils.getUpperCaseText(course.trim());
        }

        List<StudentRest2> returnValue;

        List<StudentDto> students = studentService.getStudents(page, limit, course);

        Type listType = new TypeToken<List<StudentRest2>>() {}.getType();
        returnValue = new ModelMapper().map(students, listType);

        return returnValue;
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "DELETE A STUDENT. Supply the student registration number to be deleted. This action must be authorized. Only an admin or the user that created the account can delete it.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.DeleteStudent.ApiOperation.Notes}" )
    @PreAuthorize("hasRole('ADMIN') or #regNo == principal.regNo")
    @DeleteMapping(path = "/students/{regNo}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
    @Operation(summary = "CREATE AN AWARD FOR A STUDENT. Supply the student registration number and an award to be created. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasAuthority('SUPER_WRITE_AUTHORITY')")
    @PostMapping(path = "/students/{regNo}/awards", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
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
    @Operation(summary = "GET A STUDENT AWARD. Supply the student registration number and award Id. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GeneralOne.ApiOperation.Notes}" )
    @GetMapping(path = "/students/{regNo}/awards/{awardId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AwardRest getAward(@PathVariable String regNo, @PathVariable String awardId) {

        AwardDto createdAward = awardService.getAward(regNo, awardId);

        return new ModelMapper().map(createdAward, AwardRest.class);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${studentController.authorizationHeader.description}", paramType="header",
                    required = true)
    })
    @Operation(summary = "UPDATE A STUDENT AWARD. Supply the student registration number, award Id and content of the award to be updated. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasAuthority('SUPER_WRITE_AUTHORITY')")
    @PutMapping(path = "/students/{regNo}/awards/{awardId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
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
    @Operation(summary = "GET AWARD LIST FOR A STUDENT. Supply the student registration number. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.GeneralOne.ApiOperation.Notes}" )
    @GetMapping(path = "/students/{regNo}/awards", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
    @Operation(summary = "DELETE STUDENT AWARD. Supply the student registration number and the award Id to be deleted. This action must be authorized.", security = @SecurityRequirement(name = "bearerAuth"),
            description = "${studentController.General.ApiOperation.Notes}" )
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_WRITE')")
    @DeleteMapping(path = "/students/{regNo}/awards/{awardId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteAward(@PathVariable String regNo, @PathVariable String awardId) {
        OperationStatusModel returnValue = new OperationStatusModel();

        awardService.deleteAward(regNo, awardId);

        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }


    public void checkAwardRequest(AwardRequest awardRequest) {
        if (awardRequest.getTitle().isEmpty() || awardRequest.getYear().isEmpty()){
            throw new SchoolServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }
    }

}
