package com.snipify.snipify.controller;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("mail")
public class EmailController {

    private final JavaMailSender javaMailSender;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(){
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom("atharvadev110806@gmail.com");
            message.setTo("atharvakansurkar@gmail.com");
            message.setSubject("Simple test from the spring boot app");
            message.setText("This is the test messg from the snipify spring boot application dont fear !!!!");

            javaMailSender.send(message);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        catch (Exception e){
                return new ResponseEntity<>("Failed "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/sendEmailAt")
    public ResponseEntity<String> sendMailWithAttachment(){
        long start= System.currentTimeMillis();
        try{
            MimeMessage message=javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(message,true);

            mimeMessageHelper.setFrom("atharvadev110806@gmail.com");
            mimeMessageHelper.setTo("atharvakansurkar@gmail.com");
            mimeMessageHelper.setSubject("Simple test from the spring boot app");
            mimeMessageHelper.setText("This is the test messg from the snipify spring boot application dont fear !!!!");

            mimeMessageHelper.addAttachment("attachment.pdf", new File("C:\\Users\\athar\\OneDrive\\Desktop\\sem 4temp\\SE\\attachment.pdf"));
            mimeMessageHelper.addAttachment("att.png", new File("C:\\Users\\athar\\OneDrive\\Desktop\\sem 4temp\\SE\\att.png"));
            javaMailSender.send(message);
            log.info("Time : {}",System.currentTimeMillis()-start);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
