package com.zmops.iot.media.mail;

import com.sun.mail.util.MailSSLSocketFactory;
import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author yefei
 * <p>
 * 邮件发送工具类
 **/
public class MailUtils {

    public static void send(MailSetting setting, Consumer<MimeMessage> consumer)
            throws GeneralSecurityException, javax.mail.MessagingException {

        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", setting.getHost());
        prop.setProperty("mail.smtp.port", setting.getPort().toString());
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.connectiontimeout", "2000");
        prop.setProperty("mail.smtp.writetimeout", "5000");

        if (setting.sslAvailable()) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
        }

        if (setting.tlsAvailable()) {
            prop.put("mail.smtp.ssl.checkserveridentity", "false");
            prop.put("mail.smtp.ssl.trust", setting.getHost());
            prop.put("mail.smtp.host", setting.getHost());
            prop.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(setting.getAccount(), setting.getPassword());
            }
        });

        Transport transport = session.getTransport();
        transport.connect(setting.getHost(), setting.getAccount(), setting.getPassword());

        MimeMessage mimeMessage = new MimeMessage(session);
        consumer.accept(mimeMessage);

        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        transport.close();
    }


    /**
     * 仅限于页面测试功能调用
     *
     * @param setting
     * @param receiver
     * @return
     */
    public static NoticeResult test(MailSetting setting, String receiver) {
        try {
            send(setting, mimeMessage -> {
                try {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setFrom(setting.getSender());
                    helper.setTo(receiver);
                    helper.setSubject("Zesu-iot邮件测试");
                    helper.setText("这是一封Zesu-iot测试邮件，请不要回复。", false);
                } catch (javax.mail.MessagingException e) {
                    e.printStackTrace();
                }
            });
            return NoticeResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return NoticeResult.failed(e.getMessage());
        }
    }


}
