package com.elevate5.elevateyou.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //connects to sqlite production database (database.db)
    public static Connection connect(){
        //path to the database
        var url = "jdbc:sqlite:src/main/resources/database/database.db";
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to production database successfully");
            return conn;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    //connects to sqlite test database (test_database.db)
    public static Connection connectTestDatabase(){
        //path to the database
        var url = "jdbc:sqlite:src/main/resources/database/test_database.db";
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to test database successfully");
            return conn;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }


}
