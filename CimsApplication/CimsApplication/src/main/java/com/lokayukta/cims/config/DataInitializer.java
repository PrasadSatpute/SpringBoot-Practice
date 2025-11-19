package com.lokayukta.cims.config;

import com.lokayukta.cims.entity.User;
import com.lokayukta.cims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default System Admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .email("admin@lokayukta.gov.in")
                    .mobileNumber("9999999999")
                    .role(User.Role.SYSTEM_ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
//            log.info("Default System Admin created - Username: admin, Password: admin123");
        }

        // Create sample Office User
        if (!userRepository.existsByUsername("office1")) {
            User officeUser = User.builder()
                    .username("office1")
                    .password(passwordEncoder.encode("office123"))
                    .fullName("Office User 1")
                    .email("office1@lokayukta.gov.in")
                    .mobileNumber("8888888888")
                    .role(User.Role.OFFICE_USER)
                    .active(true)
                    .build();

            userRepository.save(officeUser);
//            log.info("Sample Office User created - Username: office1, Password: office123");
        }

        // Create sample Officer
        if (!userRepository.existsByUsername("officer1")) {
            User officer = User.builder()
                    .username("officer1")
                    .password(passwordEncoder.encode("officer123"))
                    .fullName("Officer - Table 1")
                    .email("officer1@lokayukta.gov.in")
                    .mobileNumber("7777777777")
                    .role(User.Role.OFFICER)
                    .assignedSection(User.Section.LOKAYUKTA)
                    .tableNo(1)
                    .active(true)
                    .build();

            userRepository.save(officer);
//            log.info("Sample Officer created - Username: officer1, Password: officer123");
        }
    }
}