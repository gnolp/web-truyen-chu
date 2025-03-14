package com.javaweb.controller;
import com.javaweb.bean.User;
import com.javaweb.repository.UserInformation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
@Controller
public class LoginController {

	@GetMapping("/login")
	public String getLogin(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
		}
		return "login";
	}
	@PostMapping("/login")
	public RedirectView  postLogin(@RequestParam("username") String username,
							@RequestParam("password") String password,
							HttpSession session,
							Model model,
							RedirectAttributes redirectAttributes) {
		System.out.println(username + " " + password);
		if(UserInformation.checkLogin(username,password)==true) {
			User user = UserInformation.getUserInformation(username);
			session.setAttribute("user", user);
			session.setAttribute("userId", user.getId());
			model.addAttribute("userId", user.getId());
			System.out.println("id đã lưu trong model: " + model.getAttribute("userId"));
			    
			    // In giá trị userId trong session
			System.out.println("id đăng nhập là: " + user.getId());
			return new RedirectView("");
		}
		redirectAttributes.addAttribute("error", "true");
		return new RedirectView("/login");
	}
	@GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user != null) {
            response.put("loggedIn", true);
            response.put("userId", user.getId());
            response.put("firstName", user.getFirstName());
        } else {
            response.put("loggedIn", false);
        }

        return ResponseEntity.ok(response);
    }
}
