package com.javaweb.controller;
import com.javaweb.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class RegisterController {

	@GetMapping("/register")
	public String getRegister() {
		return "register";
	}
	@PostMapping("/register")
	public String postRegister(@RequestParam("username") String username,
								@RequestParam("email") String email,
								@RequestParam("password") String password,
								@RequestParam("confirm_password") String confirmPassword) {
		if(confirmPassword.equals(password)) {
			return UserInformation.addUser(username,email,password);
		}
		else return "erros: not equal ";
	}

}
