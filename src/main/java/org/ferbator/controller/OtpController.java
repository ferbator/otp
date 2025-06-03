package org.ferbator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ferbator.dto.requests.OtpRequest;
import org.ferbator.dto.requests.OtpValidateRequest;
import org.ferbator.entity.User;
import org.ferbator.service.MessagingService;
import org.ferbator.service.OtpService;
import org.ferbator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class OtpController {

    private final OtpService otpService;
    private final MessagingService messagingService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@AuthenticationPrincipal String username,
                                     @Valid @RequestBody OtpRequest request) {
        User user = userService.findByUsername(username).orElseThrow();

        var otp = otpService.generateOtp(request.getRecipient(), request.getChannel(), user);

        String text = "Ваш OTP код: " + otp.getCode();

        switch (request.getChannel().toUpperCase()) {
            case "EMAIL" -> messagingService.sendEmail(request.getRecipient(), "OTP код", text);
            case "SMS" -> messagingService.sendSms(request.getRecipient(), text);
            case "TELEGRAM" -> messagingService.sendTelegram(request.getRecipient(), text);
            case "FILE" -> otpService.saveCodeToFile(otp);
            default -> {
                return ResponseEntity.badRequest().body("Unknown channel");
            }
        }

        return ResponseEntity.ok("OTP отправлен");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateOtp(@Valid @RequestBody OtpValidateRequest request) {
        boolean valid = otpService.validateOtp(request.getRecipient(), request.getChannel(), request.getCode());
        if (valid) {
            return ResponseEntity.ok("OTP корректен");
        } else {
            return ResponseEntity.badRequest().body("Неверный или просроченный OTP");
        }
    }
}

