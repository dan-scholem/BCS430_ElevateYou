package com.elevate5.elevateyou.dao;

import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;


public class UserDao{

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, name, email, profile_image_url FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                User u = new User();
                u.setUserID(rs.getString("user_id"));

                String name = rs.getString("name");
                if (name != null && name.contains(" ")) {
                    int i = name.indexOf(' ');
                    u.setFirstName(name.substring(0, i));
                    u.setLastName(name.substring(i + 1));
                } else {
                    u.setFirstName(name);
                    u.setLastName("");
                }

                u.setEmail(rs.getString("email"));
                u.setProfileImageURL(rs.getString("profile_image_url"));
                return Optional.of(u);
            }
        }
    }

    public Optional<UserRecord> findRecordByEmail(String email) throws SQLException{
        String sql = "SELECT user_id, name, email, hash_pass, profile_image_url FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();
                    return Optional.of(new UserRecord(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("hash_pass"),
                        rs.getString("profile_image_url")
                    ));
                }
            }
        }
    
    public void insert(User u, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (name, email, hash_pass, profile_image_url) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.fullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, passwordHash);
            ps.setString(4, u.getProfileImageURL());
            ps.executeUpdate();
        }
    }
    
    public static record UserRecord(String userId, String name, String email, String passwordHash, String profileImageURL) {
        public User toUser() {
            User u = new User();
            u.setUserID(userId);
            if (name != null && name.contains(" ")) {
                int i = name.indexOf(' ');
                u.setFirstName(name.substring(0, i));
                u.setLastName(name.substring(i + 1));
            } else {
                u.setFirstName(name);
                u.setLastName("");
            }
            u.setEmail(email);
            u.setProfileImageURL(profileImageURL);
            return u;
        }
    }
}
