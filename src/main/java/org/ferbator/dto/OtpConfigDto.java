package org.ferbator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpConfigDto {
    private int length;
    private int ttlSeconds;

    public static OtpConfigDto fromEntity(org.ferbator.entity.OtpConfig entity) {
        return OtpConfigDto.builder()
                .length(entity.getCodeLength())
                .ttlSeconds(entity.getExpirationSeconds())
                .build();
    }

    public org.ferbator.entity.OtpConfig toEntity() {
        org.ferbator.entity.OtpConfig config = new org.ferbator.entity.OtpConfig();
        config.setCodeLength(this.length);
        config.setExpirationSeconds(this.ttlSeconds);
        return config;
    }
}
