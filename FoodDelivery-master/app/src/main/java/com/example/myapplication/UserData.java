package com.example.myapplication;


//Clase para los elementos del Usuario
public class UserData {
    private static int userID;
    private static String userName;
    private static String userDirection;

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int ID) {
        userID = ID;
    }
    public static String getUserDirection() {
        return userDirection;
    }

    public static void setUserDirection(String direction) {
        userDirection = direction;
    }
    }

