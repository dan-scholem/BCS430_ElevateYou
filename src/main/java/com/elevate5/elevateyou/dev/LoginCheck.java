package com.elevate5.elevateyou.dev;

import com.elevate5.elevateyou.dao.UserDao;
import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.service.AuthService;
import java.util.Random;

public class LoginCheck {
    public static void main(String[] args) throws Exception {
        var auth = new AuthService(new UserDao());
        Random random = new Random();
        int num = random.nextInt(1,6);
        User u = auth.login("testUser"+ num +"@example.com", "Password123!");
        System.out.println("Welcome " + u.getFirstName());
    }
}