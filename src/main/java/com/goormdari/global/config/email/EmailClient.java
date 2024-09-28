package com.goormdari.global.config.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClient {

    private final JavaMailSender javaMailSender;
    private final JavaMailSenderImpl mailSender;

    public void sendOneEmail(String from, String to, String joinCode, String teamName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // 수신자, 제목, 본문 설정
            String subject = from + "님이 나를 구름다리에 초대했습니다.";
            String body = from + "님이 나를 " + teamName + "팀에 초대했습니다." +
                    "<br> 아래 링크를 통해 초대를 수락주세요." +
                    "<br> 구름다리 서비스 초대코드: " + joinCode;
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(body, true);

            // 이메일 전송
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
