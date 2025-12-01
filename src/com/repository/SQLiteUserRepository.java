package com.repository;

import com.common.Database;
import com.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.services.AuthenticationService;

public class SQLiteUserRepository implements UserRepository {

    private final Database db;
    private final AuthenticationService auth;

    public SQLiteUserRepository(Database db, AuthenticationService auth) {
        this.db = db;
        this.auth = auth;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User.Builder()
                .setId(rs.getInt("id"))
                .setUsername(rs.getString("username"))
                .setEmail(rs.getString("email"))
                .setHashedPassword(rs.getString("password"))
                .setRole(parseRoleFromDb(rs.getString("role")))
                .setPhoneNumber(rs.getString("phone_number"))
                .setAddress(rs.getString("address"))
                .build();
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapRow(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapRow(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return mapRow(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findByUsernameOrEmail(String identifier) {
        String sql = """
                SELECT * FROM users
                WHERE username = ? OR email = ?
                """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, identifier);
            ps.setString(2, identifier);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int insert(User u) {

        String sql = """
                INSERT INTO users(username, email, password, role, phone_number, address, created_at)
                VALUES (?, ?, ?, ?, ?, ?, strftime('%s','now'))
                """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, formatRoleForDb(u.getRole()));
            ps.setString(5, u.getPhoneNumber());
            ps.setString(6, u.getAddress());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Insert failed (no rows)");
            }

            try {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next())
                    return keys.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException("Insert succeeded but no ID returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void update(User u) {
        String sql = """
                UPDATE users
                SET username=?, email=?, password=?, role=?, phone_number=?, address=?
                WHERE id=?
                """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, formatRoleForDb(u.getRole()));
            ps.setString(5, u.getPhoneNumber());
            ps.setString(6, u.getAddress());
            ps.setInt(7, u.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findStaffAndAdmins() {

        List<User> list = new ArrayList<>();

        String sql = """
                SELECT * FROM users
                WHERE role = 'Staff' OR role = 'CEO'
                """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next())
                list.add(mapRow(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public User validateCredentials(String usernameOrEmail, String rawPassword) {

        User u = findByUsernameOrEmail(usernameOrEmail);
        if (u == null)
            return null;

        // Use AuthenticationService to verify the raw password against stored hash
        if (!auth.verifyPassword(rawPassword, u.getPassword()))
            return null;

        return u;
    }

    // =========================================================
    // EXISTS USERNAME / EMAIL
    // =========================================================
    @Override
    public boolean existsUsername(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public boolean existsEmail(String email) {
        return findByEmail(email) != null;
    }

    // Helper methods for role conversion between DB and Java enum
    private User.Role parseRoleFromDb(String dbRole) {
        if (dbRole == null) return User.Role.CUSTOMER;
        return switch (dbRole.toLowerCase()) {
            case "customer" -> User.Role.CUSTOMER;
            case "staff" -> User.Role.STAFF;
            case "ceo" -> User.Role.CEO;
            default -> User.Role.CUSTOMER;
        };
    }

    private String formatRoleForDb(User.Role role) {
        return switch (role) {
            case CUSTOMER -> "Customer";
            case STAFF -> "Staff";
            case CEO -> "CEO";
        };
    }
}