package org.ferbator.repository;

import org.ferbator.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByRecipientAndChannelAndStatus(String recipient, String channel, OtpCode.Status status);

    List<OtpCode> findByStatus(OtpCode.Status status);

    List<OtpCode> findByUserId(Long userId);

    Boolean deleteAllByUserId(Long userId);
}
