package com.elevate5.elevateyou.dev;

import com.elevate5.elevateyou.dao.UserDao;
import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.service.AuthService;

public class LoginCheck {
    public static void main(String[] args) throws Exception {
        var auth = new AuthService(new UserDao());
        User u = auth.login("testUser1@example.com", "Password123!");
        System.out.println("Welcome " + u.getFirstName());
    }
}