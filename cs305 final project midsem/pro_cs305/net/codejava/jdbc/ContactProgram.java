package net.codejava.jdbc;

import java.sql.*;
import java.util.*;

public class ContactProgram {
    public static void main(String[] args) throws ClassNotFoundException {
        String jdbcURL = "jdbc:postgresql://localhost:5432/db";
        String username = "postgres";
        String password = "1234";

        try {
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connected to PostgreSQL server");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connecting to PostgreSQL server");
            e.printStackTrace();
        }
    }
}
