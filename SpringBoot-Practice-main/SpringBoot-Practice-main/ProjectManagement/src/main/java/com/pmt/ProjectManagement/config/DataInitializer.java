package com.pmt.ProjectManagement.config;

import com.pmt.ProjectManagement.entity.User;
import com.pmt.ProjectManagement.enums.Gender;
import com.pmt.ProjectManagement.enums.Role;
import com.pmt.ProjectManagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            //log.info("Initializing default ADMIN user...");

            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@thymewizards.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("9876543210");
            admin.setGender(Gender.MALE);
            admin.setBirthday(LocalDate.of(1990, 1, 1));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepository.save(admin);

//            log.info("Default ADMIN user created successfully!");
//            log.info("Email: admin@thymewizards.com");
//            log.info("Password: admin123");
        }
    }
}