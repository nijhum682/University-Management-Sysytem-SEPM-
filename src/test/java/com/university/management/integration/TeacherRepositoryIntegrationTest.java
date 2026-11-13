package com.university.management.integration;

import com.university.management.entity.Teacher;
import com.university.management.repository.TeacherRepository;
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
 * Integration Test 2 – TeacherRepository
 *
 * Uses H2 in-memory database (@DataJpaTest).
 * Tests real database operations for teachers.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class TeacherRepositoryIntegrationTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void cleanUp() {
        teacherRepository.deleteAll();
    }

    // ─────────────────────────────────────────────────────────
    // IT-2: Save teacher and find by email from real DB
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-2: Save teacher and retrieve by email from H2 database")
    void saveAndFindTeacherByEmail() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setName("Dr. Fatima");
        teacher.setEmail("fatima@university.com");
        teacher.setPassword("encoded_password");
        teacher.setDepartment("Mathematics");
        teacher.setDesignation("Professor");
        teacher.setRole("TEACHER");

        // Act
        teacherRepository.save(teacher);
        Optional<Teacher> found = teacherRepository.findByEmail("fatima@university.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dr. Fatima");
        assertThat(found.get().getDesignation()).isEqualTo("Professor");
    }

    // ─────────────────────────────────────────────────────────
    // IT-2b: existsByEmail returns correct boolean from real DB
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-2b: existsByEmail returns true for saved teacher, false for missing")
    void existsByEmail_correctResult() {
        // Arrange
        Teacher teacher = new Teacher();
        teacher.setName("Prof. Ali");
        teacher.setEmail("ali@university.com");
        teacher.setPassword("encoded_password");
        teacher.setDepartment("Physics");
        teacher.setDesignation("Lecturer");
        teacher.setRole("TEACHER");
        teacherRepository.save(teacher);

        // Assert
        assertThat(teacherRepository.existsByEmail("ali@university.com")).isTrue();
        assertThat(teacherRepository.existsByEmail("unknown@university.com")).isFalse();
    }

    // ─────────────────────────────────────────────────────────
    // IT-2c: Count teachers from real database
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-2c: Teacher count is correct after saving multiple records")
    void teacherCount_isCorrect() {
        // Arrange
        Teacher t1 = new Teacher();
        t1.setName("Teacher One");
        t1.setEmail("t1@university.com");
        t1.setPassword("pass");
        t1.setDepartment("CSE");
        t1.setDesignation("Lecturer");
        t1.setRole("TEACHER");

        Teacher t2 = new Teacher();
        t2.setName("Teacher Two");
        t2.setEmail("t2@university.com");
        t2.setPassword("pass");
        t2.setDepartment("EEE");
        t2.setDesignation("Senior Lecturer");
        t2.setRole("TEACHER");

        teacherRepository.save(t1);
        teacherRepository.save(t2);

        // Assert
        assertThat(teacherRepository.count()).isEqualTo(2);
    }
}
