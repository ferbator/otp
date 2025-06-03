package org.ferbator.controller;

import lombok.RequiredArgsConstructor;
import org.ferbator.dto.OtpConfigDto;
import org.ferbator.dto.UserDto;
import org.ferbator.entity.OtpConfig;
import org.ferbator.service.OtpConfigService;
import org.ferbator.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API администратора:
 * - Получение списка пользователей
 * - Удаление пользователей
 * - Изменение/получение конфигурации OTP (TTL, длина)
 */


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OtpConfigService otpConfigService;

    @GetMapping("/users")
    public List<UserDto> getAllNonAdminUsers() {
        return userService.getAllNonAdminUsers()
                .orElse(List.of())
                .stream()
                .map(UserDto::fromEntity)
                .toList();
    }


    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserByIdAndOtps(id);
    }

    @PutMapping("/otp-config")
    public OtpConfig updateOtpConfig(@RequestBody OtpConfigDto dto) {
        return otpConfigService.updateConfig(dto.getLength(), dto.getTtlSeconds());
    }

    @GetMapping("/otp-config")
    public OtpConfig getOtpConfig() {
        return otpConfigService.getConfig();
    }
}
