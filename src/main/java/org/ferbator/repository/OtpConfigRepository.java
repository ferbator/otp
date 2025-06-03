package org.ferbator.repository;

import org.ferbator.entity.OtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {
}
