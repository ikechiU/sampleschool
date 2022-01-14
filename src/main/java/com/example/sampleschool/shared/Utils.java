package com.example.sampleschool.shared;

import com.example.sampleschool.security.SecurityConstants;
import com.example.sampleschool.shared.dto.AwardDto;
import com.example.sampleschool.ui.model.response.AwardRest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class Utils {

    private final Random RANDOM = new SecureRandom();
    String NUMBER = "1234567890";

    public String generateStudentRegNo(int length) {
        return generateRegNo(length);
    }

    public String generateTeacherId(int length) {
        return generateId(length);
    }

    public String generateAwardId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    private String generateRegNo(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length())));
        }

        return "S" + Calendar.getInstance().get(Calendar.YEAR) + new String(returnValue);
    }

    private String generateId(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(NUMBER.charAt(RANDOM.nextInt(NUMBER.length())));
        }

        return "T" + Calendar.getInstance().get(Calendar.YEAR) + new String(returnValue);
    }


    public static boolean hasTokenExpired(String token) {

        boolean returnValue = false;

        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();

            returnValue = tokenExpirationDate.before(todayDate);
        } catch (ExpiredJwtException ex) {
            returnValue =  true;
        }

        return returnValue;
    }

    public String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

    public String generatePasswordResetToken(String userId)
    {
        String token = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
        return token;
    }

    private String getLowerCaseText(String text) {
        return text.toLowerCase();
    }

    public String getFirstNameDotLastName(String firstname, String lastname) {
        return (getLowerCaseText(firstname) + "." + getLowerCaseText(lastname));
    }

    public String getUpperCaseText(String text) {
        return text.toUpperCase();
    }

    public Boolean isScienceOrArt(String course) {
        return course.equals("SCIENCE") || course.equals("ART");
    }

    public Boolean isSubject(String subject) {
        return subject.equals("MATHS") || subject.equals("ENGLISH") || subject.equals("ENTREPRENEUR")
                || subject.equals("PHYSICS") || subject.equals("CHEMISTRY") || subject.equals("COMPUTER")
                || subject.equals("BIOLOGY") || subject.equals("FINE ARTS") || subject.equals("GOVERNMENT")
                || subject.equals("ECONOMICS") || subject.equals("COMMERCE");
    }

    public String getSection(String subject) {
        String returnValue = "";

        String general = "General course teacher";
        String science = "Science course teacher";
        String art = "Art course teacher";

        if (getUpperCaseText(subject).contains("MATHS") || getUpperCaseText(subject).contains("ENGLISH") ||
                getUpperCaseText(subject).contains("ENTREPRENEUR")) {
            returnValue = general;
        }

        if (getUpperCaseText(subject).contains("PHYSICS") || getUpperCaseText(subject).contains("CHEMISTRY") ||
                getUpperCaseText(subject).contains("COMPUTER") || getUpperCaseText(subject).contains("BIOLOGY")) {
            returnValue = science;
        }

        if (getUpperCaseText(subject).contains("FINE ARTS") || getUpperCaseText(subject).contains("GOVERNMENT") ||
                getUpperCaseText(subject).contains("ECONOMICS") || getUpperCaseText(subject).contains("COMMERCE")) {
            returnValue = art;
        }

        return returnValue;
    }

    public String getHouse() {
        String[] list = {"Red House", "White House", "Blue House", "Green House", "Yellow House"};
        return list[new Random().nextInt(list.length)];
    }

    public String getCapitalizeName(String name) {
        String returnValue;
        try { // We can face index out of bound exception if the string is null
            returnValue = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }catch (Exception e){
            returnValue = e.getMessage();
        }
        return returnValue;
    }

    public boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }


}
