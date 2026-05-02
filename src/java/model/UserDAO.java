package model;

import controller.Encryption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final String dbURL;
    private final String dbUser;
    private final String dbPass;
    private final String dbDriver;

    public UserDAO(String dbDriver, String dbURL, String dbUser, String dbPass) {
        this.dbDriver = dbDriver;
        this.dbURL    = dbURL;
        this.dbUser   = dbUser;
        this.dbPass   = dbPass;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(dbDriver);
        return DriverManager.getConnection(dbURL, dbUser, dbPass);
    }
    public User findByEmail(String email) throws ClassNotFoundException, SQLException {
        String sql = "SELECT EMAIL, PASSWORD, USERROLE FROM USERS WHERE EMAIL = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("EMAIL"),
                        rs.getString("PASSWORD"),
                        rs.getString("USERROLE")
                    );
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws ClassNotFoundException, SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT EMAIL, PASSWORD, USERROLE FROM USERS";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                    rs.getString("EMAIL"),
                    rs.getString("PASSWORD"),
                    rs.getString("USERROLE")
                ));
            }
        }
        return users;
    }

    public void insert(User user) throws ClassNotFoundException, SQLException {
        String sql = "INSERT INTO USERS (EMAIL, PASSWORD, USERROLE) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.executeUpdate();
        }
    }

    public void update(String originalEmail, String encryptedPassword, String role)
            throws ClassNotFoundException, SQLException {
        String sql = "UPDATE USERS SET PASSWORD = ?, USERROLE = ? WHERE EMAIL = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, encryptedPassword);
            ps.setString(2, role);
            ps.setString(3, originalEmail);
            ps.executeUpdate();
        }
    }

    public void delete(String email) throws ClassNotFoundException, SQLException {
        String sql = "DELETE FROM USERS WHERE EMAIL = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }
}