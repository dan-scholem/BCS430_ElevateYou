package com.elevate5.elevateyou.dev;

import com.elevate5.elevateyou.dao.UserDao;
import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.service.AuthService;

public class SeedUsers {
    public static void main(String[] args) throws Exception {
        UserDao userDao = new UserDao();
        AuthService auth = new AuthService(userDao);
        String pwd = "Password123!"; // same passwd

        //  6 virtual users
        for (int i = 1; i <= 6; i++) {
            create(auth, "testUser" + i, "testUser" + i + "@example.com", pwd);
        }

        System.out.println("Seed users inserted.");
    }

    private static void create(AuthService auth, String username, String email, String pwd) {
        try {
            User u = new User();
            u.setFirstName(username);   // testUserX as firstName
            u.setLastName("");          // no need lastName
            u.setEmail(email);
            auth.register(u, pwd);      // bcrypt automatically generated hash
            System.out.println("Created: " + email);
        } catch (Exception e) {
            System.out.println("Skip " + email + " -> " + e.getMessage());
        }
    }
}
