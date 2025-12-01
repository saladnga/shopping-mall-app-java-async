package com.entities;

/**
 * Represents a user in the system.
 *
 * Notes:
 * - Passwords must be stored hashed for security; never store plaintext.
 * - Role determines access control in menus and actions.
 */

public class User {
    public enum Role {
        CUSTOMER,
        STAFF,
        CEO
    }

    private int id;
    private String username;
    private String email;
    private String password; // Needs to be hased
    private Role role; // Access control in menus
    private String phoneNumber;
    private String address;

    public User() {

    };

    public User(int id, String username, String email, String password, Role role, String phoneNumber, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", role=" + role
                + '}';
    }

    public boolean isAdmin() {
        return role == Role.STAFF || role == Role.CEO;
    }

    public static class Builder {
        private int id;
        private String username;
        private String email;
        private String password;
        private User.Role role;
        private String phoneNumber;
        private String address;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setHashedPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder setPhoneNumber(String phone) {
            this.phoneNumber = phone;
            return this;
        }

        public Builder setAddress(String addr) {
            this.address = addr;
            return this;
        }

        public User build() {
            return new User(id, username, email, password, role, phoneNumber, address);
        }
    }

}
