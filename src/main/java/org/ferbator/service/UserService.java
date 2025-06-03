package org.ferbator.service;

import lombok.RequiredArgsConstructor;
import org.ferbator.entity.User;
import org.ferbator.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String password, String roleStr) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
        } catch (Exception e) {
            role = User.Role.USER;
        }

        if (role.equals(User.Role.ADMIN) && adminExists()) {
            throw new IllegalStateException("Администратор уже существует");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        return userRepository.save(user);
    }

    public boolean adminExists() {
        return userRepository.existsByRole(User.Role.ADMIN);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<List<User>> getAllNonAdminUsers() {
        return Optional.ofNullable(userRepository.findAllByRoleNot(User.Role.ADMIN));
    }

    public void deleteUserByIdAndOtps(Long id) {
        userRepository.deleteById(id);
        otpService.deleteAllByUserId(id);
    }
}

