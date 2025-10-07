package com.elevate5.elevateyou.dev;

import com.elevate5.elevateyou.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListUsers {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DatabaseConnection.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id, name, email FROM users ORDER BY user_id")) {
            System.out.println("== users table ==");
            while (rs.next()) {
                System.out.printf("%d | %s | %s%n",
                        rs.getInt("user_id"), rs.getString("name"), rs.getString("email"));
            }
        }
    }
}
