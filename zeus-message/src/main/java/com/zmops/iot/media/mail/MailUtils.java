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
 *
 **/
public class MailUtils {

    public static void send(MailSetting setting, Consumer<MimeMessage> consumer) throws GeneralSecurityException,  javax.mail.MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.host", setting.getHost());  //设置QQ邮件服务器
        prop.setProperty("mail.smtp.port", setting.getPort().toString());  //设置QQ邮件服务器
        prop.setProperty("mail.transport.protocol", "smtp"); // 邮件发送协议
        prop.setProperty("mail.smtp.auth", "true"); // 需要验证用户名密码
        prop.setProperty("mail.smtp.connectiontimeout", "2000"); // 超时
        prop.setProperty("mail.smtp.writetimeout", "5000");
        // QQ邮箱设置SSL加密
        if (setting.sslAvailable()) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
        }
        if (setting.tlsAvailable()) {
            //不做服务器证书校验
            prop.put("mail.smtp.ssl.checkserveridentity", "false");
            //添加信任的服务器地址，多个地址之间用空格分开
            prop.put("mail.smtp.ssl.trust", setting.getHost());
            prop.put("mail.smtp.host", setting.getHost());
            prop.put("mail.smtp.starttls.enable", "true");
        }
        //1、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //传入发件人的姓名和授权码
                return new PasswordAuthentication(setting.getAccount(), setting.getPassword());
            }
        });
        //2、通过session获取transport对象
        Transport transport = session.getTransport();

        //3、通过transport对象邮箱用户名和授权码连接邮箱服务器
        transport.connect(setting.getHost(), setting.getAccount(), setting.getPassword());

        //4、创建邮件,传入session对象
        MimeMessage mimeMessage = new MimeMessage(session);     // 创建邮件对象
        consumer.accept(mimeMessage);
        //5、发送邮件
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        //6、关闭连接
        transport.close();
    }


    public static NoticeResult test(MailSetting setting, String receiver) {
        try {
            send(setting, mimeMessage -> {
                try {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    helper.setFrom(setting.getSender());//发送者
                    helper.setTo(receiver);//接收者
                    helper.setSubject("Zesu-iot邮件测试");//邮件标题
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
