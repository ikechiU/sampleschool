package com.example.sampleschool.ui.controller;

import com.example.sampleschool.ui.model.request.LoginRequestModel;
import com.example.sampleschool.ui.model.request.SwaggerLoginRequestModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Operation(summary = "LOGIN. Login as a student or teacher. Check guide above.")
    @ApiOperation("Student-Teacher login")
    @ApiResponses(value = {
            @ApiResponse(code = 200,
                    message = "Response Headers",
                    responseHeaders = {
                            @ResponseHeader(name = "authorization",
                                    description = "Bearer <JWT value here>"),
                            @ResponseHeader(name = "userId",
                                    description = "<Public User Id value here>")
                    })
    })
    @PostMapping("/sample-school/login")
    public void theFakeLogin(@RequestBody SwaggerLoginRequestModel loginRequestModel)
    {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
    }
}
