package com.pmt.ProjectManagement.security;

import com.pmt.ProjectManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    public CustomUserDetails(User user) {
        this.user = user;
        System.out.println("13 CustomUserDetails Constructor Call - UserDetails set User Entity");
    }

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("19 User Have to Set Authority to Access URL from getAuthorities = "+"ROLE_" + user.getRole().name());
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        System.out.println("17 getPassword call");
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        System.out.println("21 getUsername Call");
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        System.out.println("16 isAccountNonExpired - checked");
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        System.out.println("14 isAccountNonLocked - checked");
        return user.getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        System.out.println("18 isCredentialsNonExpired - checked");
        return true;
    }

    @Override
    public boolean isEnabled() {
        System.out.println("15 isEnabled - checked");
        return user.getActive();
    }

    public User getUser() {
        System.out.println("20 getUser Call");
        return user;
    }
}