package org.ferbator.service;

import lombok.RequiredArgsConstructor;
import org.ferbator.entity.OtpConfig;
import org.ferbator.repository.OtpConfigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpConfigService {
    @Value("${otp.length}")
    private int length;

    @Value("${otp.ttl}")
    private int ttlSeconds;

    private final OtpConfigRepository configRepository;

    public OtpConfig getConfig() {
        return configRepository.findById(1L)
                .orElseGet(() -> configRepository.save(new OtpConfig(1L, length, ttlSeconds)));
    }

    public OtpConfig updateConfig(int length, int ttl) {
        OtpConfig config = getConfig();
        config.setCodeLength(length);
        config.setExpirationSeconds(ttl);
        return configRepository.save(config);
    }
}
