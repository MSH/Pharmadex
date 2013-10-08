package org.msh.pharmadex.service;

/**
 * Author: usrivastava
 */

import org.msh.pharmadex.dao.iface.MailDAO;
import org.msh.pharmadex.domain.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage alertMailMessage;

    @Autowired
    private MailDAO mailDAO;

    public void sendMail(Mail mailObj, boolean saveMail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("usrivastava@msh.org");
        message.setTo(mailObj.getMailto());
        message.setSubject(mailObj.getSubject());
        message.setText(mailObj.getMessage());
        message.setSentDate(mailObj.getDate());

        mailSender.send(message);
        if(saveMail)
            mailDAO.save(mailObj);
    }

    public void sendAlertMail(String alert) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(alertMailMessage);
        mailMessage.setText(alert);
        mailSender.send(mailMessage);
    }

    public List<Mail> findAllMailSent(Long prodAppId){
        return mailDAO.findByProdApplications_IdOrderByDateDesc(prodAppId);
    }

}


