package com.mig.reporting_server.controller;

import com.mig.reporting_server.entity.ReportTemplate;
import com.mig.reporting_server.service.ReportingService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class PageController {

    private final ReportingService service;

    public PageController(ReportingService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String reportsPage(
            org.springframework.ui.Model model) {
        model.addAttribute("templates", service.findAll());
        return "reports";
    }

    @PostMapping("/reports")
    public String addReport(
            @RequestParam String name,
            @RequestParam MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            service.add(name, file);
            redirectAttributes.addFlashAttribute(
                    "success", "Report template uploaded successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "error", "A template with this name already exists.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Unable to save the uploaded file.");
        }

        return "redirect:/";
    }


    @PostMapping("/reports/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            service.update(id, name, file);
            redirectAttributes.addFlashAttribute(
                    "success", "Template updated successfully.");
        } catch (IOException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/reports/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute(
                    "success", "Template deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
    }
}
