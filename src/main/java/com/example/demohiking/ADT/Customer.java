package com.example.demohiking.ADT;

public class Customer {
    private String id;
    private String nama;
    private String jeniskelamin;
    private String nomortelephone;
    private String email;
    private String alamat;

    public Customer(String id, String nama, String nomortelephone, String email) {
        this.id = id;
        this.nama = nama;
        this.jeniskelamin = null;
        this.nomortelephone = nomortelephone;
        this.email = email;
        this.alamat = null;
    }

    public Customer(String id, String nama, String jeniskelamin, String nomortelephone, String email, String alamat) {
        this.id = id;
        this.nama = nama;
        this.jeniskelamin = jeniskelamin;
        this.nomortelephone = nomortelephone;
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

    public String getJeniskelamin() {
        return jeniskelamin;
    }

    public void setJeniskelamin(String jeniskelamin) {
        this.jeniskelamin = jeniskelamin;
    }

    public String getNomortelephone() {
        return nomortelephone;
    }

    public void setNomortelephone(String nomortelephone) {
        this.nomortelephone = nomortelephone;
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
