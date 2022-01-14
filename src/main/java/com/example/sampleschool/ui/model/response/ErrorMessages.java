package com.example.sampleschool.ui.model.response;

public enum ErrorMessages {

    RECORD_ALREADY_EXISTS("Record already exists."),
    NAME_LENGTH_SHORT("Name length is less than two."),
    EMAIL_ALREADY_EXISTS("Email already exists."),
    RESERVED_EMAIL("Administration domain email."),
    INVALID_EMAIL("Invalid email."),
    CONTACT_ADMIN_EMAIL_ISSUE("Contact admin to create your account."),
    SUBJECT_TEACHER_ALREADY_EXIST("Subject teacher already exist. Contact admin."),
    MISSING_REQUIRED_FIELD("Missing required field."),
    COURSE_TYPE_ERROR("Course can only be SCIENCE or ART."),
    SUBJECT_TYPE_ERROR("Select one subject: MATHS, ENGLISH, ENTREPRENEUR, PHYSICS, CHEMISTRY, COMPUTER, " +
            "BIOLOGY, FINE ARTS, GOVERNMENT, ECONOMICS, COMMERCE"),
    NO_AWARD_FOUND("No award found."),
    NO_RECORD_FOUND("No record found.");



    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
