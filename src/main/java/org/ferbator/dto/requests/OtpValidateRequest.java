package org.ferbator.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpValidateRequest {
    @NotBlank
    private String recipient;

    @NotBlank
    private String channel;

    @NotBlank
    private String code;
}
