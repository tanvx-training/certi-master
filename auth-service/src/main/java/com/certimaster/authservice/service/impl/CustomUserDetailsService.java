package com.certimaster.authservice.service.impl;

import com.certimaster.authservice.entity.User;
import com.certimaster.authservice.repository.UserRepository;
import com.certimaster.commonlibrary.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!Status.ACTIVE.getDescription().equalsIgnoreCase(user.getStatus()))
                .build();
    }


    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles()
                .stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getCode()))
                .collect(Collectors.toSet());
    }
}
