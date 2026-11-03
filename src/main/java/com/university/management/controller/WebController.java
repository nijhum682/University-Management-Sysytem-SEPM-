package com.university.management.controller;

import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import com.university.management.service.AdminService;
import com.university.management.service.StudentService;
import com.university.management.service.TeacherService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final AdminService adminService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public WebController(AdminService adminService,
                         TeacherService teacherService,
                         StudentService studentService,
                         TeacherRepository teacherRepository,
                         StudentRepository studentRepository) {
        this.adminService = adminService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_TEACHER".equals(role)) {
                return "redirect:/teacher/dashboard";
            } else if ("ROLE_STUDENT".equals(role)) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    // --- Admin Pages ---

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        model.addAttribute("adminCount", adminService.count());
        model.addAttribute("teacherCount", teacherService.count());
        model.addAttribute("studentCount", studentService.count());
        model.addAttribute("admins", adminService.getAll());
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("students", studentService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "admin/dashboard";
    }

    @GetMapping("/admin/admins")
    public String adminAdmins(Model model, Authentication auth) {
        model.addAttribute("admins", adminService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "admin/admins";
    }

    @GetMapping("/admin/teachers")
    public String adminTeachers(Model model, Authentication auth) {
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "admin/teachers";
    }

    @GetMapping("/admin/students")
    public String adminStudents(Model model, Authentication auth) {
        model.addAttribute("students", studentService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "admin/students";
    }

    // --- Teacher Pages ---

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard(Model model, Authentication auth) {
        teacherRepository.findByEmail(auth.getName()).ifPresent(teacher -> model.addAttribute("currentTeacher", teacher));
        model.addAttribute("teacherCount", teacherService.count());
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "teacher/dashboard";
    }

    @GetMapping("/teacher/teachers")
    public String teacherTeachers(Model model, Authentication auth) {
        model.addAttribute("teachers", teacherService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "teacher/teachers";
    }

    // --- Student Pages ---

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Authentication auth) {
        studentRepository.findByEmail(auth.getName()).ifPresent(student -> model.addAttribute("currentStudent", student));
        model.addAttribute("studentCount", studentService.count());
        model.addAttribute("students", studentService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "student/dashboard";
    }

    @GetMapping("/student/students")
    public String studentStudents(Model model, Authentication auth) {
        model.addAttribute("students", studentService.getAll());
        model.addAttribute("currentUser", auth.getName());
        return "student/students";
    }
}
