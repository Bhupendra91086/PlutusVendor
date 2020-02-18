package com.app.plutusvendorapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String NUMBER_PATTERN = "(\\+)[0-9]{6}[0-9]*";


    public static boolean EmailValidator(String text) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean NumberValidator(String text) {
        Pattern pattern = Pattern.compile(NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
