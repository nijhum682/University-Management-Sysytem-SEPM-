package com.university.management.service;

import com.university.management.dto.StudentDto;
import com.university.management.entity.Student;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          AdminRepository adminRepository,
                          TeacherRepository teacherRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StudentDto> getAll() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return toDto(student);
    }

    public StudentDto create(StudentDto dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for new student");
        }
        checkEmailUnique(dto.getEmail(), null);

        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists: " + dto.getStudentId());
        }

        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPassword(passwordEncoder.encode(dto.getPassword()));
        student.setStudentId(dto.getStudentId());
        student.setDepartment(dto.getDepartment());
        student.setSemester(dto.getSemester());
        student.setRole("STUDENT");

        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    public StudentDto update(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        checkEmailUnique(dto.getEmail(), student.getEmail());

        if (!student.getStudentId().equalsIgnoreCase(dto.getStudentId()) &&
            studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new IllegalArgumentException("Student ID already exists: " + dto.getStudentId());
        }

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setStudentId(dto.getStudentId());
        student.setDepartment(dto.getDepartment());
        student.setSemester(dto.getSemester());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Student updated = studentRepository.save(student);
        return toDto(updated);
    }

    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    public long count() {
        return studentRepository.count();
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

    public StudentDto toDto(Student entity) {
        String role = entity.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return new StudentDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getStudentId(),
                entity.getDepartment(),
                entity.getSemester(),
                role
        );
    }
}
