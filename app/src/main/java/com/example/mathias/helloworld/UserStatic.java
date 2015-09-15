package com.example.mathias.helloworld;

/**
 * Created by Mathias on 15-09-2015.
 */
public class UserStatic {
    private static String name = "default";
    private static String email = "default";

    public static void setEmail(String email) {
        UserStatic.email = email;
    }

    public static void setName(String name) {
        UserStatic.name = name;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

}
