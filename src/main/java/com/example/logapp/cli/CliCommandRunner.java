package com.example.logapp.cli;

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
            case "add-user" -> {
                configAdminService.addUser(args[1], args[2], args[3]);
                System.out.println("User added: " + args[1]);
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
