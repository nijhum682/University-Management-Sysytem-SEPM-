package com.university.management.service;

import com.university.management.dto.AdminDto;
import com.university.management.entity.Admin;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test 3 – AdminService
 *
 * Tests 1 scenario (admin create with duplicate email) without touching any database.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    // ─────────────────────────────────────────────────────────
    // Unit Test 3.1 – Create admin fails when email is already used
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("UT-6: Create admin fails when email is already registered in the system")
    void createAdmin_duplicateEmail_throwsException() {
        // Arrange: email already exists in adminRepository
        AdminDto dto = new AdminDto(null, "Super Admin", "admin@university.com", null);
        dto.setPassword("adminPass");

        when(adminRepository.existsByEmail("admin@university.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> adminService.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already registered");

        verify(adminRepository, never()).save(any(Admin.class));
    }
}
