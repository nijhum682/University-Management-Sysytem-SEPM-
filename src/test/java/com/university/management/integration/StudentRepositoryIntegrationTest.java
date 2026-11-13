package com.university.management.integration;

import com.university.management.entity.Student;
import com.university.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration Test 1 – StudentRepository
 *
 * Uses H2 in-memory database (@DataJpaTest).
 * Tests real database save, find, and delete operations.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class StudentRepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanUp() {
        studentRepository.deleteAll();
    }

    // ─────────────────────────────────────────────────────────
    // IT-1: Save and retrieve student from database
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-1: Save student and find by email from real H2 database")
    void saveAndFindStudentByEmail() {
        // Arrange
        Student student = new Student();
        student.setName("Nadia Islam");
        student.setEmail("nadia@test.com");
        student.setPassword("hashed_password");
        student.setStudentId("STU-101");
        student.setDepartment("BBA");
        student.setSemester("4th");
        student.setRole("STUDENT");

        // Act
        studentRepository.save(student);
        Optional<Student> found = studentRepository.findByEmail("nadia@test.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Nadia Islam");
        assertThat(found.get().getStudentId()).isEqualTo("STU-101");
        assertThat(found.get().getDepartment()).isEqualTo("BBA");
    }

    // ─────────────────────────────────────────────────────────
    // IT-1b: existsByStudentId works correctly
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-1b: existsByStudentId returns true for existing, false for missing")
    void existsByStudentId_correctResult() {
        // Arrange
        Student student = new Student();
        student.setName("Tanvir Ahmed");
        student.setEmail("tanvir@test.com");
        student.setPassword("hashed_pass");
        student.setStudentId("STU-202");
        student.setDepartment("EEE");
        student.setSemester("2nd");
        student.setRole("STUDENT");
        studentRepository.save(student);

        // Act & Assert
        assertThat(studentRepository.existsByStudentId("STU-202")).isTrue();
        assertThat(studentRepository.existsByStudentId("STU-999")).isFalse();
    }

    // ─────────────────────────────────────────────────────────
    // IT-1c: Delete student removes from database
    // ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("IT-1c: Delete student removes record from H2 database")
    void deleteStudent_removesFromDatabase() {
        // Arrange
        Student student = new Student();
        student.setName("Sabbir Khan");
        student.setEmail("sabbir@test.com");
        student.setPassword("hashed_pass");
        student.setStudentId("STU-303");
        student.setDepartment("ME");
        student.setSemester("7th");
        student.setRole("STUDENT");
        Student saved = studentRepository.save(student);

        // Act
        studentRepository.deleteById(saved.getId());
        List<Student> remaining = studentRepository.findAll();

        // Assert
        assertThat(remaining).isEmpty();
    }
}
