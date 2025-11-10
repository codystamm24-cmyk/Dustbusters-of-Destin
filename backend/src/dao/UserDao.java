package dao;

import model.User;
import config.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, first_name, last_name, phone, created_at, updated_at FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone"),
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: " + email, e);
            throw new RuntimeException("Database error", e);
        }
        
        return Optional.empty();
    }

    public boolean verifyPassword(String email, String password) {
        String sql = "SELECT password_hash FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return BCrypt.checkpw(password, storedHash);
                }
            }
        } catch (SQLException e) {
            logger.error("Error verifying password for user: " + email, e);
            throw new RuntimeException("Database error", e);
        }
        
        return false;
    }

    public User createUser(String email, String password, String firstName, String lastName, String phone) {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id, created_at, updated_at";
        
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            
            stmt.setString(1, email);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getLong("id"),
                        email,
                        firstName,
                        lastName,
                        phone,
                        rs.getTimestamp("created_at").toInstant(),
                        rs.getTimestamp("updated_at").toInstant()
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating user: " + email, e);
            throw new RuntimeException("Database error", e);
        }
        
        throw new RuntimeException("Failed to create user");
    }
}