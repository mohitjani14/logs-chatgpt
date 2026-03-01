package com.example.logapp.config;

import java.util.ArrayList;
import java.util.List;

public class UsersFile {
    private List<UserRecord> users = new ArrayList<>();

    public List<UserRecord> getUsers() { return users; }
    public void setUsers(List<UserRecord> users) { this.users = users; }
}
