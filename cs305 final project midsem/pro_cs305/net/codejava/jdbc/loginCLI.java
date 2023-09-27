package net.codejava.jdbc;

import net.codejava.jdbc.studentcli;
import java.sql.*;
import java.util.Scanner;

//public class loginCLI {

public class loginCLI {
    public static void main(String[] args) throws SQLException{

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "postgres", "1234");
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("Unable to connect to database: " + e.getMessage());
            System.exit(1);
        }
        Scanner scanner = new Scanner(System.in);

        // Print the login options
        System.out.println("Press 1 to login as a student");
        System.out.println("Press 2 to login as a faculty");
        System.out.println("Press 3 to login as academic office");

        // Take user input
        int option = scanner.nextInt();

        // Handle the user input
        switch (option) {
            case 1:
                // Login as a student
                studentcli cli = new studentcli();
                cli.run(connection);
                break;
            case 2:
                // Login as a faculty
                facultycli fcli = new facultycli();
                fcli.run(connection);
                break;
            case 3:
                // Login as an academic office
                acadofficecli acli = new acadofficecli();
                acli.initializeConnection();
                acli.run(connection);
                break;
        }
        scanner.close();
    }
}

    