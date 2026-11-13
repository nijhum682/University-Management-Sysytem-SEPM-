package com.university.management.service;

import com.university.management.dto.TeacherDto;
import com.university.management.entity.Teacher;
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
 * Unit Test 2 – TeacherService
 *
 * Tests 2 scenarios without touching any database (Mockito mocks everything).
 */
@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher sampleTeacher;

    @BeforeEach
    void setUp() {
        sampleTeacher = new Teacher();
        sampleTeacher.setId(10L);
        sampleTeacher.setName("Prof. Hasan");
        sampleTeacher.setEmail("hasan@university.com");
        sampleTeacher.setPassword("encoded");
        sampleTeacher.setDepartment("CSE");
        sampleTeacher.setDesignation("Associate Professor");
        sampleTeacher.setRole("TEACHER");
    }

    // ─────────────────────────────────────────────────────────
    // Unit Test 2.1 – Create teacher successfully
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-4: Create teacher successfully when all data is valid")
    void createTeacher_success() {
        // Arrange
        TeacherDto dto = new TeacherDto(null, "Prof. Hasan", "hasan@university.com",
                "CSE", "Associate Professor", null);
        dto.setPassword("securePass");

        when(adminRepository.existsByEmail("hasan@university.com")).thenReturn(false);
        when(teacherRepository.existsByEmail("hasan@university.com")).thenReturn(false);
        when(studentRepository.existsByEmail("hasan@university.com")).thenReturn(false);
        when(passwordEncoder.encode("securePass")).thenReturn("encoded");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(sampleTeacher);

        // Act
        TeacherDto result = teacherService.create(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Prof. Hasan");
        assertThat(result.getDepartment()).isEqualTo("CSE");
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    // ─────────────────────────────────────────────────────────
    // Unit Test 2.2 – Get teacher by ID – not found case
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-5: Get teacher by ID throws ResourceNotFoundException when not found")
    void getTeacherById_notFound_throwsException() {
        // Arrange
        when(teacherRepository.findById(500L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> teacherService.getById(500L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Teacher not found");
    }
}
