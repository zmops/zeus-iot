package com.zmops.iot.media.mail;


import com.zmops.iot.domain.messages.MailSetting;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.util.ToolUtil;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 **/
@Service
public class MailNoticer implements MailNotice {

    @Autowired
    MailSettingService mailSettingService;
    @Autowired
    private FreeMarkerConfigurer configurer;

    @Override
    public NoticeResult test(MailSetting setting, String receiver) {
        return MailUtils.test(setting, receiver);
    }


    @Override
    public NoticeResult send(SysUser receiver, Map<String, String> macroMap) {
        if (ToolUtil.isEmpty(receiver.getEmail())) {
            return NoticeResult.skipped();
        }
        MailSetting setting = mailSettingService.get();
        if (setting == null) {
            return NoticeResult.skipped();
        }
        if (!setting.getSeverity().contains(macroMap.get("${severity}"))) {
            return NoticeResult.skipped();
        }
        Integer problemId = Integer.parseInt(macroMap.get("${problemId}"));

        String alarmInfo = "【Zeus】 触发[${level}]告警“[${context}]”，[${time}]。请及时处理。";
        for (Map.Entry<String, String> entry : macroMap.entrySet()) {
            String value = Optional.ofNullable(entry.getValue()).orElse("");
            alarmInfo = alarmInfo.replace(entry.getKey(), value);
        }

        try {
            MailUtils.send(setting, mimeMessage -> {
                try {
                    String subject = "触发${level}告警",
                            content = "【Zeus】 触发[${level}]告警“[${context}]”，[${time}]。请及时处理。\n";
                    for (Map.Entry<String, String> entry : macroMap.entrySet()) {
                        String value = Optional.ofNullable(entry.getValue()).orElse("");
                        subject = subject.replace(entry.getKey(), value);
                        content = content.replace(entry.getKey(), value);
                    }
                    MailMessage message = new MailMessage();
                    message.setUser(receiver.getName());
                    message.setMessage(content);
                    message.setAlarmRule(macroMap.get("${metricName}"));
                    message.setLevel(macroMap.get("${level}"));
                    message.setObjectType(macroMap.get("${typeName}"));
                    message.setServer(macroMap.get("${topMoType}"));
                    message.setTime(macroMap.get("${time}"));
//                    message.setTable(values.toString());
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setFrom(setting.getSender());//发送者
                    helper.setTo(receiver.getEmail());//接收者
                    helper.setSubject(subject);//邮件标题
                    Map<String, Object> model = new HashMap<>();
                    model.put("params", message);
                    Template template = configurer.getConfiguration().getTemplate("argusAlarmEmailTemplate.html", "UTF-8");
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
                    helper.setText(text, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return NoticeResult.failed("发送失败: " + StringUtils.left(e.getMessage(), 100), alarmInfo, receiver.getEmail());
        }
        return NoticeResult.success(alarmInfo, receiver.getEmail());
    }

    @Override
    public int getType() {
        return email;
    }

    @Override
    public int getSilent() {
        MailSetting setting = mailSettingService.get();
        return Optional.ofNullable(setting.getSilent()).orElse(3);
    }
}
