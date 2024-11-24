package com.javaweb.service;
import java.security.SecureRandom;
import com.javaweb.service.MailSender;

public class PasswordResetEmail {

	public static String generateVerificationCode(int n) {
		 String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        SecureRandom random = new SecureRandom();
	        StringBuilder otp = new StringBuilder(n);

	        for (int i = 0; i < n; i++) {
	            int index = random.nextInt(characters.length());
	            otp.append(characters.charAt(index));
	        }
	        return otp.toString();
	}
	public static void sendEmailReset(String email, String verificationsCode) {
		MailSender.sendEmail(email, "Mã xác thực mật khẩu", verificationsCode);
	}
}
