package com.ccpd.forestsun.service;

import javax.mail.MessagingException;
import java.util.List;

/**
 * @author forestsun
 * @date 2018/12/29
 */
public interface MailService {
    void sendTextMails(String to, String subject, String content);

    void sendAttachmentMail(String to, String subject, String content, List<String> filePathList) throws MessagingException;
}
