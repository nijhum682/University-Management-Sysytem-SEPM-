package com.university.management.service;

import com.university.management.dto.AdminDto;
import com.university.management.entity.Admin;
import com.university.management.exception.ResourceNotFoundException;
import com.university.management.repository.AdminRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@SuppressWarnings("null")
public class AdminService {

    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        TeacherRepository teacherRepository,
                        StudentRepository studentRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminDto> getAll() {
        return adminRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AdminDto getById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
        return toDto(admin);
    }

    public AdminDto create(AdminDto dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for new admin");
        }
        checkEmailUnique(dto.getEmail(), null);

        Admin admin = new Admin();
        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole("ADMIN");

        Admin saved = adminRepository.save(admin);
        return toDto(saved);
    }

    public AdminDto update(Long id, AdminDto dto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));

        checkEmailUnique(dto.getEmail(), admin.getEmail());

        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Admin updated = adminRepository.save(admin);
        return toDto(updated);
    }

    public void delete(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
        adminRepository.delete(admin);
    }

    public long count() {
        return adminRepository.count();
    }

    private void checkEmailUnique(String newEmail, String currentEmail) {
        if (currentEmail != null && currentEmail.equalsIgnoreCase(newEmail)) {
            return;
        }
        if (adminRepository.existsByEmail(newEmail) ||
            teacherRepository.existsByEmail(newEmail) ||
            studentRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already registered in the system: " + newEmail);
        }
    }

    public AdminDto toDto(Admin entity) {
        String role = entity.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return new AdminDto(entity.getId(), entity.getName(), entity.getEmail(), role);
    }
}
