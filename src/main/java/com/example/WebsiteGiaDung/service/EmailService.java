package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.model.User; // Đảm bảo dùng đúng User của bạn
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String siteURL) throws MessagingException {
        String toAddress = user.getEmail();  // Sử dụng phương thức getEmail() từ User của bạn
        String subject = "Xác nhận đăng ký tài khoản";
        String content = "Xin chào [[name]],<br>"
                + "Vui lòng nhấn vào liên kết sau để xác nhận đăng ký tài khoản của bạn:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">XÁC NHẬN</a></h3>"
                + "Cảm ơn,<br>WebsiteGiaDung";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(toAddress);
        helper.setSubject(subject);

        // Thay thế nội dung
        content = content.replace("[[name]]", user.getUsername());  // Hoặc user.getName() nếu có
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }
}
