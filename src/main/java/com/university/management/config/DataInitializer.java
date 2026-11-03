package com.university.management.config;

import com.university.management.entity.Admin;
import com.university.management.entity.Student;
import com.university.management.entity.Teacher;
import com.university.management.repository.AdminRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("null")
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed default Admin
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setName("System Admin");
            admin.setEmail("admin@university.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            adminRepository.save(admin);
            System.out.println(">>> Seeded default Admin: admin@university.com / admin123");
        }

        // Seed default Teacher
        if (teacherRepository.count() == 0) {
            Teacher teacher = new Teacher();
            teacher.setName("Dr. Alan Turing");
            teacher.setEmail("teacher@university.com");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setDepartment("Computer Science");
            teacher.setDesignation("Professor");
            teacher.setRole("TEACHER");
            teacherRepository.save(teacher);
            System.out.println(">>> Seeded default Teacher: teacher@university.com / teacher123");
        }

        // Seed default Student
        if (studentRepository.count() == 0) {
            Student student = new Student();
            student.setName("Alice Johnson");
            student.setEmail("student@university.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setStudentId("STU-1001");
            student.setDepartment("Computer Science");
            student.setSemester("4th");
            student.setRole("STUDENT");
            studentRepository.save(student);
            System.out.println(">>> Seeded default Student: student@university.com / student123");
        }
    }
}
