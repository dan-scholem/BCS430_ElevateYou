package com.elevate5.elevateyou.service;

import com.elevate5.elevateyou.dao.UserDao;
import com.elevate5.elevateyou.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) { this.userDao = userDao; }

    public void register(User u, String rawPassword) throws Exception {
        Optional<User> existing = userDao.findByEmail(u.getEmail());
        if (existing.isPresent()) {
            throw new IllegalStateException("Email already registered");
        }
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
        userDao.insert(u, hash);
    }

    public User login(String email, String rawPassword) throws Exception {
        var recOpt = userDao.findRecordByEmail(email);
        if (recOpt.isEmpty()) throw new IllegalArgumentException("User not found");
        var rec = recOpt.get();
        if (!BCrypt.checkpw(rawPassword, rec.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return rec.toUser();
    }
}
