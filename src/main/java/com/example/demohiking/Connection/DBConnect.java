package com.example.demohiking.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Hiking";
        String user = "tanto";
        String password = "tanto";

        return DriverManager.getConnection(url, user, password);
    }
}