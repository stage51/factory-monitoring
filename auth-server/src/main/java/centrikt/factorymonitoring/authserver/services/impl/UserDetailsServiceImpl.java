package centrikt.factorymonitoring.authserver.services.impl;

import centrikt.factorymonitoring.authserver.exceptions.EntityNotFoundException;
import centrikt.factorymonitoring.authserver.models.User;
import centrikt.factorymonitoring.authserver.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("UserDetailsServiceImpl initialized");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.debug("UserRepository set");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        log.trace("Entering loadUserByUsername method with username: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", username);
                    return new EntityNotFoundException("User not found with email: " + username);
                });

        log.debug("User found with email: {}", username);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        log.info("Successfully loaded user details for username: {}", username);

        log.trace("Exiting loadUserByUsername method with user details: {}", userDetails);
        return userDetails;
    }
}

