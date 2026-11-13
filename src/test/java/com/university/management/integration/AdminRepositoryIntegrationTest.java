package com.university.management.integration;

import com.university.management.entity.Admin;
import com.university.management.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Test 3 – AdminRepository
 *
 * Uses H2 in-memory database (@DataJpaTest).
 * Tests real database operations for admins.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class AdminRepositoryIntegrationTest {

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void cleanUp() {
        adminRepository.deleteAll();
    }

    // ─────────────────────────────────────────────────────────
    // IT-3: Save admin and find by email from real DB
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-3: Save admin and retrieve by email from H2 database")
    void saveAndFindAdminByEmail() {
        // Arrange
        Admin admin = new Admin();
        admin.setName("Super Admin");
        admin.setEmail("superadmin@university.com");
        admin.setPassword("hashed_admin_pass");
        admin.setRole("ADMIN");

        // Act
        adminRepository.save(admin);
        Optional<Admin> found = adminRepository.findByEmail("superadmin@university.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Super Admin");
        assertThat(found.get().getRole()).isEqualTo("ADMIN");
    }

    // ─────────────────────────────────────────────────────────
    // IT-3b: existsByEmail works for admins in real DB
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-3b: existsByEmail returns correct result for admin records")
    void existsByEmail_correctResult() {
        // Arrange
        Admin admin = new Admin();
        admin.setName("Admin One");
        admin.setEmail("admin1@university.com");
        admin.setPassword("pass");
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        // Assert
        assertThat(adminRepository.existsByEmail("admin1@university.com")).isTrue();
        assertThat(adminRepository.existsByEmail("nobody@university.com")).isFalse();
    }

    // ─────────────────────────────────────────────────────────
    // IT-3c: Update admin name persists in real DB
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-3c: Update admin name and confirm change is persisted")
    void updateAdmin_nameIsPersisted() {
        // Arrange
        Admin admin = new Admin();
        admin.setName("Old Name");
        admin.setEmail("updatable@university.com");
        admin.setPassword("pass");
        admin.setRole("ADMIN");
        Admin saved = adminRepository.save(admin);

        // Act
        saved.setName("New Name");
        adminRepository.save(saved);

        // Assert
        Optional<Admin> updated = adminRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("New Name");
    }
}
