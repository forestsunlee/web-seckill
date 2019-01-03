package com.ccpd.forestsun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

/**
 * @author forestsun
 * @date 2018/12/29
 */
@Service
public class MailServiceImpl  implements MailService{

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 发送文本邮件
     * @param to 发送到。。
     * @param subject 主题
     * @param content  邮件内容
     */
    @Override
    public void sendTextMails(String to, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        simpleMailMessage.setFrom(from);
        javaMailSender.send(simpleMailMessage);
    }

    /**
     * 发送带附件的邮件(可以发送多附件)
     * @param to
     * @param subject
     * @param content
     * @param filePathList  附件的本地目录
     */
    @Override
    public void sendAttachmentMail(String to, String subject, String content, List<String> filePathList) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content);
        for(String filePath : filePathList){
            FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
            String filename = fileSystemResource.getFilename();
            helper.addAttachment(filename,fileSystemResource);
        }
        javaMailSender.send(mimeMessage);
    }

}
