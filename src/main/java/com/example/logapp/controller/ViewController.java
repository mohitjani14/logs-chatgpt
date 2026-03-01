package com.example.logapp.controller;

import com.example.logapp.config.ServerConfig;
import com.example.logapp.dto.DownloadRequest;
import com.example.logapp.service.ConfigAdminService;
import com.example.logapp.service.DownloadService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewController {
    private final ConfigAdminService configAdminService;
    private final DownloadService downloadService;

    public ViewController(ConfigAdminService configAdminService, DownloadService downloadService) {
        this.configAdminService = configAdminService;
        this.downloadService = downloadService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        model.addAttribute("projects", configAdminService.listProjects());
        model.addAttribute("request", new DownloadRequest());
        model.addAttribute("jobs", downloadService.userJobs(authentication.getName()));
        return "index";
    }

    @PostMapping("/download")
    public String download(@Valid @ModelAttribute("request") DownloadRequest request,
                           BindingResult bindingResult,
                           Authentication authentication,
                           Model model) {
        model.addAttribute("projects", configAdminService.listProjects());
        model.addAttribute("jobs", downloadService.userJobs(authentication.getName()));
        if (bindingResult.hasErrors()) {
            return "index";
        }
        String jobId = downloadService.queueJob(authentication.getName(), request);
        model.addAttribute("jobId", jobId);
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        enrichAdminModel(model);
        return "admin";
    }

    @PostMapping("/admin/project")
    public String addProject(@RequestParam String name, RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.addProject(name);
        redirectAttributes.addFlashAttribute("message", "Project added");
        return "redirect:/admin";
    }

    @PostMapping("/admin/environment")
    public String addEnvironment(@RequestParam String project, @RequestParam String environment, RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.addEnvironment(project, environment);
        redirectAttributes.addFlashAttribute("message", "Environment added");
        return "redirect:/admin";
    }

    @PostMapping("/admin/module")
    public String addModule(@RequestParam String project, @RequestParam String environment, @RequestParam String module, RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.addModule(project, environment, module);
        redirectAttributes.addFlashAttribute("message", "Module added");
        return "redirect:/admin";
    }

    @PostMapping("/admin/server")
    public String addServer(@RequestParam String project,
                            @RequestParam String environment,
                            @RequestParam String module,
                            @RequestParam String serverName,
                            @RequestParam String host,
                            @RequestParam int port,
                            @RequestParam String username,
                            @RequestParam String logDirectory,
                            @RequestParam String logPattern,
                            RedirectAttributes redirectAttributes) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName(serverName);
        serverConfig.setHost(host);
        serverConfig.setPort(port);
        serverConfig.setUsername(username);
        serverConfig.setLogDirectory(logDirectory);
        serverConfig.setLogPattern(logPattern);
        serverConfig.setRotationSupported(true);
        configAdminService.addServer(project, environment, module, serverConfig);
        redirectAttributes.addFlashAttribute("message", "Server added or updated");
        return "redirect:/admin";
    }

    @PostMapping("/admin/user")
    public String addUser(@RequestParam String username, @RequestParam String password, @RequestParam String role, RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.addUser(username, password, role);
        redirectAttributes.addFlashAttribute("message", "User added or updated");
        return "redirect:/admin";
    }

    @PostMapping("/admin/system")
    public String system(@RequestParam int workerThreads,
                         @RequestParam int queueCapacity,
                         @RequestParam String downloadDirectory,
                         @RequestParam int downloadTimeoutSeconds,
                         RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.updateSettings(workerThreads, queueCapacity, downloadDirectory, downloadTimeoutSeconds);
        redirectAttributes.addFlashAttribute("message", "System settings updated");
        return "redirect:/admin";
    }

    @PostMapping("/admin/reset-temp-password")
    public String resetTemporaryPassword(@RequestParam(defaultValue = "TempAdmin@123") String temporaryPassword,
                                         RedirectAttributes redirectAttributes) throws Exception {
        configAdminService.resetTemporaryAdminPassword(temporaryPassword);
        redirectAttributes.addFlashAttribute("message", "Temporary admin password reset");
        return "redirect:/admin";
    }

    private void enrichAdminModel(Model model) {
        model.addAttribute("projects", configAdminService.listProjects());
        model.addAttribute("users", configAdminService.listUsers());
        model.addAttribute("system", configAdminService.settings());
        model.addAttribute("jobs", downloadService.allJobs());
    }
}
