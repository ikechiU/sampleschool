package com.example.sampleschool.exceptions;

public class SchoolServiceException extends RuntimeException{

    private static final long serialVersionUID = 1348771109171435607L;

    public SchoolServiceException(String message)
    {
        super(message);
    }
}