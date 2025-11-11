package com.pmt.ProjectManagement.config;

import com.pmt.ProjectManagement.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        System.out.println("2 SecurityConfig Constructor Call - userDetailsService set");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("3 PasswordEncoder Bean Created");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        System.out.println("5 DaoAuthenticationProvider Bean Created");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        System.out.println("6 DaoAuthenticationProvider - setUserDetailsService(userDetailsService) and setPasswordEncoder");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        System.out.println("9 AuthenticationManager Call to Authenticate");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("7 SecurityFilterChain Call");
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/tasks/new", "/tasks/*/edit", "/tasks/*/delete", "/tasks")
                        .hasAnyRole("ADMIN", "CEO", "MANAGER")
                        // Teams: only ADMIN, CEO, MANAGER can create/edit/delete
                        .requestMatchers("/teams/create", "/teams/edit/**", "/teams/delete/**")
                        .hasAnyRole("ADMIN", "CEO", "MANAGER")
                        .requestMatchers("/calendar").permitAll()

                        // Document permissions
                        .requestMatchers("/documents/create", "documents/upload-multiple", "/documents/edit/**", "/documents/delete/**")
                        .hasAnyRole("ADMIN", "CEO", "MANAGER")
                        .requestMatchers("/documents/**")
                        .authenticated()

                        // Teams: all authenticated users can view
                        .requestMatchers("/teams/**").authenticated()
                        .requestMatchers("/tasks/updateCompletion/*").authenticated()
                        .requestMatchers("/tasks/**").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/projects/create", "/projects/edit/**", "/projects/delete/**")
                        .hasAnyRole("ADMIN", "CEO", "MANAGER")
                        .requestMatchers("/projects/**").authenticated()
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );

        http
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin() // Or disable completely: .disable()
                        )
                );

        System.out.println("8 SecurityFilterChain Complete");


        return http.build();
    }
}