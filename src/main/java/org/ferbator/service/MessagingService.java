package org.ferbator.service;

import lombok.RequiredArgsConstructor;
import org.ferbator.service.messagingServices.MailService;
import org.ferbator.service.messagingServices.SmppService;
import org.ferbator.service.messagingServices.TelegramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    private final MailService mailService;
    private final SmppService smppService;
    private final TelegramService telegramService;

    public void sendEmail(String to, String subject, String text) {
        try {
            mailService.sendOtp(to, text);
            logger.info("Email sent to {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

    public void sendSms(String phone, String text) {
        smppService.sendOtp(phone, text);
        logger.info("SMS to {}: {}", phone, text);
    }

    public void sendTelegram(String chatId, String text) {
        telegramService.sendOtp(chatId, text);
        logger.info("Telegram to {}: {}", chatId, text);
    }
}

