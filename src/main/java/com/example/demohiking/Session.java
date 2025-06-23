package com.example.demohiking;

public class Session {
    private static String id;
    private static String nama;
    private static String role;

    public static void setSession(String npkValue, String namaValue, String roleValue) {
        id = npkValue;
        nama = namaValue;
        role = roleValue;
    }

    public static boolean isLoggedIn() {
        return id != null && nama != null && role != null;
    }

    public static String getId() {
        return id;
    }

    public static String getNama() {
        return nama;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        id = null;
        nama = null;
        role = null;
    }
}