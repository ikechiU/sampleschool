package com.example.sampleschool.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.example.sampleschool.shared.dto.StudentDto;
import org.springframework.stereotype.Service;

@Service
public class AmazonSES {
    // This address must be verified with Amazon SES.
    final String FROM = "[....]"; //supply your own verified email address

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your registration with Sample School";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Kudos for registering with  Sample School. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='[....]?token=$tokenValue'>" //create your own webservice host
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    final String TEXTBODY = "Please verify your email address. "
            + "Kudos for registering with success contribution mobile app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + " https://[....]?token=$tokenValue"  //create your own webservice host
            + " Thank you!";

    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with Sample School. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='[....]'>" //create your own webservice host
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    final String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with Sample School. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"  //create your own webservice host
            + " https://[....]?token=$tokenValue"
            + " Thank you!";


    public void verifyEmail(StudentDto studentDto) {
        awsMasked();

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_2) //ADD YOUR REGION ***VERY IMPORTANT****
                .build();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", studentDto.getEmailVerificationToken());
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", studentDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(studentDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");

    }

    private void awsMasked() {
        // You can also set your keys this way. And it will work! AVOID UPLOADING IN PUBLIC
        System.setProperty("aws.accessKeyId", "[....]");  //ADD YOUR accessKeyId ***VERY IMPORTANT****
        System.setProperty("aws.secretKey", "[....]");  //ADD YOUR secretKey ***VERY IMPORTANT****
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token)
    {
        awsMasked();
        boolean returnValue = false;

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_2).build();  //ADD YOUR REGION ***VERY IMPORTANT****

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);


        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses( email ) )
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBodyWithToken))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBodyWithToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);
        if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty()))
        {
            returnValue = true;
        }

        return returnValue;
    }

}
