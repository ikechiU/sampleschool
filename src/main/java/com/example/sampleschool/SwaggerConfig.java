package com.example.sampleschool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    Contact contact = new Contact(
            "Ikechi Ucheagwu",
            "https://ikechiu.github.io/",
            "ikechi@hotmail.com"
    );

    List<VendorExtension> vendorExtensions = new ArrayList<>();

    ApiInfo apiInfo = new ApiInfo(
            "A Sample School RESTful Web Service documentation.",
            "This pages documents Sample School app RESTful Web Service endpoints.\n\n\n" +

                    "******<u>TEST CREDENTIAL</u>******\n" +
                    "email: <b>super.write@school.com</b> \n" +
                    "teacherId: <b>T2022939285465215033</b>\n" +
                    "password: <b>123456789</b>\n\n\n" +

                    "******<u>CREATE AN ACCOUNT</u>******\n" +
                    "Create a student account with a valid email.\n" +
                    "<b>Check your inbox or spam for registration confirmation mail.</b>\n\n\n" +

                    "******<u>LOGIN</u>******\n" +
                    "email and password &nbsp; or &nbsp; teacherId and password &nbsp; or &nbsp; regNo and password\n" +
                    "<b>All created accounts must be verified before login.</b>",
            "1.0",
            "http://www.appsdeveloperblog.com/service.html",
            contact,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            vendorExtensions);


    @Bean
    public Docket apiDocket() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo) //Extra documentation features
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.sampleschool"))
                .paths(PathSelectors.any())
                .build();

        return docket;

    }

}
