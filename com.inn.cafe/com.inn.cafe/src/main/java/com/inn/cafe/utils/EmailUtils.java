package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
@Component
public class EmailUtils {


    @Autowired
    private JavaMailSender javaMailSender;


    public void sendSimpleMessage(String to, String subject, String text, List<String> list){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("countrydeights@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if(list!=null && list.size()>0){
            message.setCc(ccArray(list));
        }

        javaMailSender.send(message);



    }

    private String[] ccArray(List<String> list){
        String[] cc = new String[list.size()];
        for(int i=0;i<list.size();i++){

            cc[i] = list.get(i);

        }
        return cc;
    }

    public void forgotMail(String to, String subject, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String htmlMsg = "<p><b>Your Login Details for Cafe Management System</b><br><b>Email: </b>" + to + "<br><b>Password: </b>" + password + "<br><a href=\"http://localhost:4200/\">Click Here to Login </a></p>";

        helper.setText(htmlMsg, true); // Set to true to indicate the message is in HTML format
        helper.setFrom("countrydeights@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        javaMailSender.send(mimeMessage);
    }


}
