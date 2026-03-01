package com.example.logapp.config;

import java.util.ArrayList;
import java.util.List;

public class ModuleConfig {
    private String name;
    private List<ServerConfig> servers = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ServerConfig> getServers() { return servers; }
    public void setServers(List<ServerConfig> servers) { this.servers = servers; }
}
