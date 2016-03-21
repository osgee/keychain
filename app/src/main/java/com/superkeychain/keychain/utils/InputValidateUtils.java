package com.superkeychain.keychain.utils;

/**
 * Created by taofeng on 3/19/16.
 */
public class InputValidateUtils {
    public static boolean isUsername(String username) {
        return username.matches("(\\w|\\d){3,20}");
    }

    public static boolean isInputLengthEnough(String username) {

        return username.matches("(\\w|\\d){6,20}");
    }

    public static boolean isEmail(String email) {

        return email.matches("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
    }

    public static boolean isCellphone(String cellphone) {

        return cellphone.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    }

    public static boolean isInputAllDigits(String username) {
        return username.matches("//d{10,13}");
    }
}
