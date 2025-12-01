package com.managers.account;

import com.services.AuthenticationService;
import com.entities.User;
import com.repository.UserRepository;

/**
 * Handles login authentication logic.
 */

public class LoginManager {

    private final UserRepository userRepo;
    private final AuthenticationService auth;

    public LoginManager(UserRepository repo, AuthenticationService auth) {
        this.userRepo = repo;
        this.auth = auth;
    }

    public User login(String usernameOrEmail, String password) throws Exception {

        // UserRepository must provide this method
        User user = userRepo.findByUsernameOrEmail(usernameOrEmail);

        if (user == null)
            throw new Exception("User not found");

        // Compare raw password with hashed password
        if (!auth.verifyPassword(password, user.getPassword()))
            throw new Exception("Incorrect password");

        return user;
    }
}