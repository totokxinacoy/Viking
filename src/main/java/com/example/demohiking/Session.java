package com.example.demohiking;

public class Session {
    private static String npk;
    private static String nama;
    private static String role;

    public static void setSession(String npkValue, String namaValue, String roleValue) {
        npk = npkValue;
        nama = namaValue;
        role = roleValue;
    }

    public static boolean isLoggedIn() {
        return npk != null && nama != null && role != null;
    }

    public static String getNpk() {
        return npk;
    }

    public static String getNama() {
        return nama;
    }

    public static String getRole() {
        return role;
    }

    public static void clearSession() {
        npk = null;
        nama = null;
        role = null;
    }
}