package org.ferbator.service.messagingServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmppService {

    private static final Logger logger = LoggerFactory.getLogger(SmppService.class);

    @Value("${smpp.host}")
    private String host;

    @Value("${smpp.port}")
    private int port;

    @Value("${smpp.system_id}")
    private String systemId;

    @Value("${smpp.password}")
    private String password;

    @Value("${smpp.system_type}")
    private String systemType;

    @Value("${smpp.source_addr}")
    private String sourceAddr;

    public void sendOtp(String username, String otp) {
        Connection connection = null;
        Session session = null;

        try {
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);

            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddr);

            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new RuntimeException("SMPP bind failed with status: " + bindResponse.getCommandStatus());
            }

            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddr);
            submitSM.setDestAddr(username);
            submitSM.setShortMessage("Your OTP code is: " + otp);

            session.submit(submitSM);
            logger.info("SMPP message sent to {}", username);

        } catch (Exception e) {
            logger.error("Failed to send SMPP message", e);
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                try {
                    session.unbind();
                    session.close();
                } catch (Exception e) {
                    logger.warn("Failed to close SMPP session", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.warn("Failed to close SMPP connection", e);
                }
            }
        }
    }
}
