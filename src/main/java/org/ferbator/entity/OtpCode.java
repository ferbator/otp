package org.ferbator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String recipient; // Email, phone или telegram chatId

    private String channel; // EMAIL, SMS, TELEGRAM, FILE

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    @ManyToOne
    private User user;

    public enum Status {
        ACTIVE,
        EXPIRED,
        USED
    }
}
