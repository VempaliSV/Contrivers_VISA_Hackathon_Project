package com.example.visa_project.utils;

import android.graphics.Color;

public enum PasswordStrength {

    // we use some color in green tint =>
    //more secure is the password, more darker is the color associated
    WEAK("Weak", Color.parseColor("#cccc00")),
    MEDIUM("Medium", Color.parseColor("#009900")),
    STRONG("Strong", Color.parseColor("#990000")),
    VERY_STRONG("Very Strong", Color.parseColor("#1B3F9A"));

    public String msg;
    public long color;
    private static int MIN_LENGTH = 8;
    private static int MAX_LENGTH = 15;

    PasswordStrength(String msg, int color) {
        this.msg = msg;
        this.color = color;
    }

    public static PasswordStrength calculate(String password) {
        int score = 0;
        // boolean indicating if password has an upper case
        boolean upper = false;
        // boolean indicating if password has a lower case
        boolean lower = false;
        // boolean indicating if password has at least one digit
        boolean digit = false;
        // boolean indicating if password has a leat one special char
        boolean specialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (!specialChar  &&  !Character.isLetterOrDigit(c)) {
                score++;
                specialChar = true;
            } else {
                if (!digit  &&  Character.isDigit(c)) {
                    score++;
                    digit = true;
                } else {
                    if (!upper || !lower) {
                        if (Character.isUpperCase(c)) {
                            upper = true;
                        } else {
                            lower = true;
                        }

                        if (upper && lower) {
                            score++;
                        }
                    }
                }
            }
        }

        int length = password.length();

        if (length > MAX_LENGTH) {
            score++;
        } else if (length < MIN_LENGTH) {
            score = 0;
        }

        // return enum following the score
        switch(score) {
            case 0 : return WEAK;
            case 1 : return MEDIUM;
            case 2 : return STRONG;
            case 3 : return VERY_STRONG;
            default:
        }

        return VERY_STRONG;
    }
}