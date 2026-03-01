package com.example.logapp.cli;

import com.example.logapp.config.ServerConfig;
import com.example.logapp.service.ConfigAdminService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CliCommandRunner implements CommandLineRunner {
    private final ConfigAdminService configAdminService;

    public CliCommandRunner(ConfigAdminService configAdminService) {
        this.configAdminService = configAdminService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            return;
        }
        switch (args[0]) {
            case "add-project" -> {
                configAdminService.addProject(args[1]);
                System.out.println("Project added: " + args[1]);
                System.exit(0);
            }
            case "add-environment" -> {
                configAdminService.addEnvironment(args[1], args[2]);
                System.out.println("Environment added: " + args[2]);
                System.exit(0);
            }
            case "add-module" -> {
                configAdminService.addModule(args[1], args[2], args[3]);
                System.out.println("Module added: " + args[3]);
                System.exit(0);
            }
            case "add-server" -> {
                ServerConfig s = new ServerConfig();
                s.setName(args[4]);
                s.setHost(args[5]);
                s.setPort(Integer.parseInt(args[6]));
                s.setUsername(args[7]);
                s.setLogDirectory(args[8]);
                s.setLogPattern(args[9]);
                s.setRotationSupported(true);
                configAdminService.addServer(args[1], args[2], args[3], s);
                System.out.println("Server added: " + s.getName());
                System.exit(0);
            }
            case "add-user" -> {
                configAdminService.addUser(args[1], args[2], args[3]);
                System.out.println("User added: " + args[1]);
                System.exit(0);
            }
            case "set-temp-admin-password" -> {
                configAdminService.resetTemporaryAdminPassword(args[1]);
                System.out.println("Temporary admin password updated.");
                System.exit(0);
            }
            case "reload-config" -> {
                configAdminService.reloadConfig();
                System.out.println("Configuration reloaded.");
                System.exit(0);
            }
            default -> System.out.println("Unknown command");
        }
    }
}
