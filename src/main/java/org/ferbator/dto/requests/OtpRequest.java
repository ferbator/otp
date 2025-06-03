package org.ferbator.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
    @NotBlank
    private String recipient;

    @NotBlank
    private String channel; // EMAIL, SMS, TELEGRAM, FILE
}

