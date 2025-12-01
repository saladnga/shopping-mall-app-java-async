package com.managers.account;

import com.services.AuthenticationService;
import com.entities.User;
import com.repository.UserRepository;

public class EditAccountManager {
    private final AuthenticationService auth;
    private final UserRepository repo;

    public EditAccountManager(UserRepository repo, AuthenticationService auth) {
        this.repo = repo;
        this.auth = auth;
    }

    public User editUser(User editor, int targetUserId,
            String newName, String newEmail,
            String newPassword, String newPhone,
            String newAddress) throws Exception {

        User target = repo.findById(targetUserId);
        if (target == null)
            throw new Exception("User not found");

        // Permissions
        if (editor.getRole() == User.Role.CUSTOMER && editor.getId() != targetUserId)
            throw new Exception("Customers cannot edit other accounts");

        if (editor.getRole() == User.Role.STAFF && target.getRole() == User.Role.CEO)
            throw new Exception("Staff cannot modify CEO accounts");

        // Username
        if (newName != null && !newName.isBlank()) {
            if (!newName.equals(target.getUsername()) &&
                    repo.existsUsername(newName))
                throw new Exception("Username already exists");

            target.setUsername(newName);
        }

        // Email
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(target.getEmail()) &&
                    repo.existsEmail(newEmail))
                throw new Exception("Email already registered");

            target.setEmail(newEmail);
        }

        // Password
        if (newPassword != null && !newPassword.isBlank()) {
            target.setPassword(auth.hashPassword(newPassword));
        }

        // Phone
        if (newPhone != null && !newPhone.isBlank()) {
            target.setPhoneNumber(newPhone);
        }

        // Address
        if (newAddress != null && !newAddress.isBlank()) {
            target.setAddress(newAddress);
        }

        repo.update(target);
        return target;
    }
}