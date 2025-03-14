package com.javaweb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.javaweb.bean.PasswordUpdateRequest;
import com.javaweb.bean.User;
import com.javaweb.repository.UserInformation;
import com.javaweb.service.PasswordResetEmail;

@Controller
public class ResetPassword {
	@GetMapping("forgot-password")
	public String getForms() {
		return "resetPassword";
	}
	@GetMapping("/search-users")
	public ResponseEntity<List<User>> getUsername(@RequestParam String email){
	    List<User> users = UserInformation.searchUser(email);
	    if (!users.isEmpty()) {
	        return ResponseEntity.ok(users);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	@PostMapping("/sendEmail")
	public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> requestBody) {
	    Map<String, String> response = new HashMap<>();

	    try {
	     
	    	String verificationCode = PasswordResetEmail.generateVerificationCode(6);
	        PasswordResetEmail.sendEmailReset(requestBody.get("email"), verificationCode);
	        // Lưu mã xác thực vào response
	        response.put("code", verificationCode);
	        response.put("message", "Verification code sent successfully!");
	        
	        return ResponseEntity.ok(response); // Trả về mã 200 OK cùng với response
	    } catch (Exception e) {
	        response.put("message", "Failed to send verification code: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Trả về mã 500 nếu có lỗi
	    }
	}
	@PostMapping("/resetPassword")
	public ResponseEntity<Map<String,String>> updatePassword(@RequestBody PasswordUpdateRequest request){
		System.out.println("doi tai khoan:" +request.getUsername());
		UserInformation.changePassword(request.getPassword(), request.getUsername());
		Map<String, String> response = new HashMap<>();
	    response.put("message", "Password updated successfully");
	    return ResponseEntity.ok(response);
	}
	@PostMapping("/change-password")
	public ResponseEntity<Map<String, Object>> changePassword(
	        @RequestParam("id") int id,
	        @RequestBody Map<String, String> mp) {
	    
	    String newPassword = mp.get("newPassword");
	    boolean ok = UserInformation.changePassword(newPassword, id);

	    if (ok) {
	        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
	    } else {
	        return ResponseEntity.status(500).body(Map.of("success", false, "message", "Change password failed"));
	    }
	}
}
