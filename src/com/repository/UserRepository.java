package com.repository;

import com.entities.User;
import java.util.List;

public interface UserRepository {

    /** Find user by ID */
    User findById(int id);

    /** Find user by username */
    User findByUsername(String username);

    /** Find user by email */
    User findByEmail(String email);

    /** Find user by username OR email (for login) */
    User findByUsernameOrEmail(String identifier);

    /** Insert new user — MUST return generated ID */
    int insert(User user);

    /** Update existing user */
    void update(User user);

    /** Remove user */
    void delete(int id);

    /** Get staff + CEO for messaging */
    List<User> findStaffAndAdmins();

    /** Validate login */
    User validateCredentials(String usernameOrEmail, String rawPassword);

    /** Check username exists */
    boolean existsUsername(String username);

    /** Check email exists */
    boolean existsEmail(String email);
}