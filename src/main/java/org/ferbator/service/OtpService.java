package org.ferbator.service;

import lombok.RequiredArgsConstructor;
import org.ferbator.entity.OtpCode;
import org.ferbator.entity.User;
import org.ferbator.repository.OtpCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис генерации и валидации OTP-кодов.
 * - Генерирует коды с заданной длиной и TTL
 * - Проверяет и помечает коды как USED или EXPIRED
 * - Планово деактивирует просроченные коды
 */


@Service
@RequiredArgsConstructor
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final OtpCodeRepository otpCodeRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 5;

    public OtpCode generateOtp(String recipient, String channel, User user) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        OtpCode otp = OtpCode.builder()
                .code(code)
                .recipient(recipient)
                .channel(channel)
                .status(OtpCode.Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES))
                .user(user)
                .build();

        return otpCodeRepository.save(otp);
    }

    public boolean validateOtp(String recipient, String channel, String code) {
        Optional<OtpCode> opt = otpCodeRepository.findByRecipientAndChannelAndStatus(recipient, channel, OtpCode.Status.ACTIVE);

        if (opt.isEmpty()) return false;

        OtpCode otp = opt.get();

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OtpCode.Status.EXPIRED);
            otpCodeRepository.save(otp);
            return false;
        }

        if (!otp.getCode().equals(code)) {
            return false;
        }

        otp.setStatus(OtpCode.Status.USED);
        otpCodeRepository.save(otp);
        return true;
    }

    public void saveCodeToFile(OtpCode code) {
        try {
            Path path = Paths.get("otp-codes.log");
            String line = String.format("Time: %s, User: %s, Code: %s%n",
                    LocalDateTime.now(), code.getUser().getUsername(), code.getCode());
            Files.writeString(path, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("Error saving OTP-code to file: {}", e.getMessage());
        }
    }

    public Boolean deleteAllByUserId(Long id) {
        return otpCodeRepository.deleteAllByUserId(id);
    }

    @Scheduled(cron = "0 * * * * *") // Каждую минуту
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateExpiredOtps() {
        List<OtpCode> activeOtps = otpCodeRepository.findByStatus(OtpCode.Status.ACTIVE);
        if (activeOtps.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<OtpCode> expiredOtps = activeOtps.stream()
                .filter(otp -> now.isAfter(otp.getExpiresAt()))
                .peek(otp -> {
                    otp.setStatus(OtpCode.Status.EXPIRED);
                    logger.info("OTP-code {} is EXPIRED", otp.getCode());
                })
                .toList();

        if (!expiredOtps.isEmpty()) {
            otpCodeRepository.saveAll(expiredOtps);
            logger.info("Update {} expired OTP-codes", expiredOtps.size());
        }
    }
}
