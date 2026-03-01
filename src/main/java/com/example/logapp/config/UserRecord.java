package com.example.logapp.config;

import com.example.logapp.model.Role;

public class UserRecord {
    private String username;
    private String passwordHash;
    private Role role = Role.USER;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
