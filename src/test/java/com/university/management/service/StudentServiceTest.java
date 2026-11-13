package com.university.management.service;

import com.university.management.dto.StudentDto;
import com.university.management.entity.Student;
import com.university.management.exception.ResourceNotFoundException;
import com.university.management.repository.AdminRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test 1 – StudentService
 *
 * Tests 3 scenarios without touching any database (Mockito mocks everything).
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student();
        sampleStudent.setId(1L);
        sampleStudent.setName("Rahim Uddin");
        sampleStudent.setEmail("rahim@example.com");
        sampleStudent.setPassword("encodedPass");
        sampleStudent.setStudentId("STU-001");
        sampleStudent.setDepartment("CSE");
        sampleStudent.setSemester("6th");
        sampleStudent.setRole("STUDENT");
    }

    // ─────────────────────────────────────────────────────────
    // Unit Test 1.1 – Create a student successfully
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-1: Create student successfully when all data is valid")
    void createStudent_success() {
        // Arrange
        StudentDto dto = new StudentDto(null, "Rahim Uddin", "rahim@example.com",
                "STU-001", "CSE", "6th", null);
        dto.setPassword("rawPassword123");

        when(adminRepository.existsByEmail("rahim@example.com")).thenReturn(false);
        when(teacherRepository.existsByEmail("rahim@example.com")).thenReturn(false);
        when(studentRepository.existsByEmail("rahim@example.com")).thenReturn(false);
        when(studentRepository.existsByStudentId("STU-001")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPass");
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        // Act
        StudentDto result = studentService.create(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Rahim Uddin");
        assertThat(result.getEmail()).isEqualTo("rahim@example.com");
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    // ─────────────────────────────────────────────────────────
    // Unit Test 1.2 – Create student should throw if no password
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-2: Create student fails when password is missing")
    void createStudent_missingPassword_throwsException() {
        // Arrange
        StudentDto dto = new StudentDto(null, "Karim", "karim@example.com",
                "STU-002", "EEE", "2nd", null);
        dto.setPassword(""); // blank password

        // Act & Assert
        assertThatThrownBy(() -> studentService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password is required");

        verify(studentRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────
    // Unit Test 1.3 – Get student by ID – not found case
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-3: Get student by ID throws ResourceNotFoundException when ID does not exist")
    void getStudentById_notFound_throwsException() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student not found");
    }
}
