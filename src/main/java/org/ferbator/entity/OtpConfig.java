package org.ferbator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "otp_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpConfig {

    @Id
    @Builder.Default
    private Long id = 1L;

    private int codeLength;

    private int expirationSeconds;
}

