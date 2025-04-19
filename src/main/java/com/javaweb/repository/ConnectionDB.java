package com.javaweb.repository;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;


public class ConnectionDB {
	
	private static String DB_URL = "jdbc:sqlserver://PNLONG\\\\S1:2513;"
            + "databaseName=auth;"
            + "encrypt=false;";
    private static String USER_NAME = "long";
    private static String PASSWORD = "Password123#";
    protected static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
    }
}
