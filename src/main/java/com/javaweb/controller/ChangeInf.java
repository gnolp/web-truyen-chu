package com.javaweb.controller;
import com.javaweb.bean.User;
import com.javaweb.repository.UserInformation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;
@Controller
public class ChangeInf {
	
	@ResponseBody
	@GetMapping("/user-info/{id}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable("id") int requestedUserId,
            HttpSession session) {
		
					Integer sessionUserId = (int) session.getAttribute("userId");
					System.out.println("day la trong user-info:" + sessionUserId);
					boolean isSelf = sessionUserId != null && sessionUserId==requestedUserId;
					User user = UserInformation.getUserInformation(requestedUserId);
		        
					if (user == null) {
						System.out.println("null roi");
			            return ResponseEntity.notFound().build();
			        }

			        Map<String, Object> response = new HashMap<>();
			        response.put("name", user.getFirstName()+" "+user.getLastName());
			        response.put("email", user.getEmail());
			        response.put("phonenumber", user.getPhonenumber());
			        response.put("butdanh", user.getButdanh());
			        response.put("gioitinh", user.getGioitinh());
			        if(user.getScrA()!=null)
			        	response.put("srcA", user.getScrA().replace("\\", "/"));
			        else response.put("srcA", user.getScrA());
			        response.put("namsinh", user.getNamsinh());
			        response.put("firstName",user.getFirstName());
			        response.put("lastName",user.getLastName());
			        response.put("isSelf", isSelf);
			        for(String x: response.keySet()) {
			        	System.out.println(x +"	 : "+response.get(x));
			        }
			        return ResponseEntity.ok(response);
			    }
}
