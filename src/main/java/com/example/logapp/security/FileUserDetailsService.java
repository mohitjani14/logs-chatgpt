package com.example.logapp.security;

import com.example.logapp.config.FileConfigStore;
import com.example.logapp.config.UserRecord;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileUserDetailsService implements UserDetailsService {
    private final FileConfigStore fileConfigStore;

    public FileUserDetailsService(FileConfigStore fileConfigStore) {
        this.fileConfigStore = fileConfigStore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRecord record = fileConfigStore.getUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(record.getUsername(), record.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + record.getRole().name())));
    }
}
