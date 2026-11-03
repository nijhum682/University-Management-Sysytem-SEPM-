package com.university.management.service;

import com.university.management.dto.TeacherDto;
import com.university.management.entity.Teacher;
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
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository teacherRepository,
                          AdminRepository adminRepository,
                          StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<TeacherDto> getAll() {
        return teacherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TeacherDto getById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        return toDto(teacher);
    }

    public TeacherDto create(TeacherDto dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for new teacher");
        }
        checkEmailUnique(dto.getEmail(), null);

        Teacher teacher = new Teacher();
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());
        teacher.setRole("TEACHER");

        Teacher saved = teacherRepository.save(teacher);
        return toDto(saved);
    }

    public TeacherDto update(Long id, TeacherDto dto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));

        checkEmailUnique(dto.getEmail(), teacher.getEmail());

        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setDepartment(dto.getDepartment());
        teacher.setDesignation(dto.getDesignation());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Teacher updated = teacherRepository.save(teacher);
        return toDto(updated);
    }

    public void delete(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
        teacherRepository.delete(teacher);
    }

    public long count() {
        return teacherRepository.count();
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

    public TeacherDto toDto(Teacher entity) {
        String role = entity.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return new TeacherDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getDepartment(),
                entity.getDesignation(),
                role
        );
    }
}
