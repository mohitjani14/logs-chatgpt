package com.example.logapp.controller;

import com.example.logapp.config.FileConfigStore;
import com.example.logapp.dto.DownloadRequest;
import com.example.logapp.service.DownloadService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {
    private final FileConfigStore fileConfigStore;
    private final DownloadService downloadService;

    public ViewController(FileConfigStore fileConfigStore, DownloadService downloadService) {
        this.fileConfigStore = fileConfigStore;
        this.downloadService = downloadService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("projects", fileConfigStore.getProjects());
        model.addAttribute("request", new DownloadRequest());
        return "index";
    }

    @PostMapping("/download")
    public String download(@Valid @ModelAttribute("request") DownloadRequest request,
                           BindingResult bindingResult,
                           Authentication authentication,
                           Model model) {
        model.addAttribute("projects", fileConfigStore.getProjects());
        if (bindingResult.hasErrors()) {
            return "index";
        }
        String jobId = downloadService.queueJob(authentication.getName(), request);
        model.addAttribute("jobId", jobId);
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("projects", fileConfigStore.getProjects());
        model.addAttribute("users", fileConfigStore.getUsers());
        return "admin";
    }
}
