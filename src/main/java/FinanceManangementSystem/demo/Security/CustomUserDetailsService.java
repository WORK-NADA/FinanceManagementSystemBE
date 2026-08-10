package FinanceManangementSystem.demo.Security;

import FinanceManangementSystem.demo.Model.User;
import FinanceManangementSystem.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        log.debug("CustomUserDetailsService - Loading user details for email: {}", email);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> {

                    log.warn("CustomUserDetailsService - User not found with email: {}", email);

                    return new UsernameNotFoundException(
                            "User not found with email: " + email
                    );
                });

        log.info("CustomUserDetailsService - User loaded successfully: {}", email);

        log.debug("CustomUserDetailsService - Assigned role: {}", user.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(
                        new SimpleGrantedAuthority(
                                user.getRole().name()
                        )
                )
        );
    }
}