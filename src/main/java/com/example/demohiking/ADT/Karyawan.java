package com.example.demohiking.ADT;

public class Karyawan {
    private String id;
    private String nama;
    private String password;
    private String id_jabatan;
    private String email;
    private String alamat;

    public Karyawan(String id, String nama, String id_jabatan, String email) {
        this.id = id;
        this.nama = nama;
        this.password = null;
        this.id_jabatan = id_jabatan;
        this.email = email;
        this.alamat = null;
    }

    public Karyawan(String id, String nama, String password, String id_jabatan, String email, String alamat) {
        this.id = id;
        this.nama = nama;
        this.password = password;
        this.id_jabatan = id_jabatan;
        this.email = email;
        this.alamat = alamat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId_jabatan() {
        return id_jabatan;
    }

    public void setId_jabatan(String id_jabatan) {
        this.id_jabatan = id_jabatan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
