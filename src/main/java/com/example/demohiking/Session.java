package com.example.demohiking;

public class Session {
    private static String npk;
    private static String namaKaryawan;

    public static void setSession(String npk, String namaKaryawan) {
        Session.npk = npk;
        Session.namaKaryawan = namaKaryawan;
    }

    public static String getNpk() {
        return npk;
    }

    public static String getNamaKaryawan() {
        return namaKaryawan;
    }

    public static boolean isLoggedIn() {
        return npk != null && namaKaryawan != null;
    }

    public static void clearSession() {
        npk = null;
        namaKaryawan = null;
    }
}