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
            "https://iykeafrica.github.io/",
            "ikechi@hotmail.com"
    );

    List<VendorExtension> vendorExtensions = new ArrayList<>();

    ApiInfo apiInfo = new ApiInfo(
            "A Sample School RESTful Web Service documentation.",
            "This pages documents Sample School app RESTful Web Service endpoints." +

                    "<h4>******TEST CREDENTIAL******</h4>" +
                    "EMAIL: &nbsp; <b>super.write@school.com</b> \n" +
                    "ID: &nbsp; <b>T2022939285465215033</b>\n" +
                    "PASSWORD: &nbsp; <b>123456789</b>" +

                    "<h4>******LOGIN******</h4>" +
                    "You can Login with either:&nbsp;&nbsp; EMAIL and PASSWORD &nbsp;&nbsp; or  &nbsp;&nbsp;  ID and PASSWORD" +

                    "<h4>******CREATE AN ACCOUNT******</h4>" +
            "When you create a student account <b><u>please note: &nbsp; you will need to confirm the email address before you can login &nbsp; (check your inbox or spam for registration confirmation mail.)</u></b>",
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
