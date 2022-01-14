package com.example.sampleschool.security;


import com.example.sampleschool.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; //10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; //1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String REG_NO = "Reg No";
    public static final String TEACHER_ID = "Teacher Id";
    public static final String LOGIN_ROLE = "Login Role";
    public static final String FIRST_NAME = "Firstname";
    public static final String LAST_NAME = "Lastname";
    public static final String REGISTER_STUDENT = "/sample-school/student";
    public static final String REGISTER_TEACHER = "/sample-school/teacher";
    public static final String EMAIL_VERIFICATION_URL = "/sample-school/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/sample-school/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/sample-school/password-reset";
    public static final String H2_CONSOLE = "/h2-console/**";
//    public static final String TOKEN_SECRET = "jf9i4qgu8mfl0"; added in application properties

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
