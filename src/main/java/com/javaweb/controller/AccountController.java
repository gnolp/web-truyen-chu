package com.javaweb.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.util.Value;
import com.javaweb.bean.Book;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.UserInformation;
import com.javaweb.service.CloudinaryService;
import com.sun.mail.iap.Response;
@Controller
public class AccountController {
	private final CloudinaryService cloudinaryService;

    public AccountController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    @GetMapping("/account")
    public String  getAccount(@RequestParam("userId") long userId, Model model, HttpSession session ) {
        User user = (User) session.getAttribute("user");
        User userI = UserInformation.getUserInformation(userId);
        if(user != null) {
        	model.addAttribute("userId", userId);
        	model.addAttribute("name",userI.getFirstName()+" "+userI.getLastName());
        	System.out.println("model: "+model.getAttribute("userId") + ", " + model.getAttribute("name"));
        	return "canhan2";
        }
        else {
        	return "loginPlease";
        }
    }
   
   

        @GetMapping("/stories-of-user")
        public ResponseEntity<List<Book>> getUserStories(@RequestParam("userId") long userId) throws SQLException {
            
        	List<Book> stories = BookInformation.getstories(userId);
        	return ResponseEntity.ok(stories);
        }

        @GetMapping("/bookshelf-of-user")
        public ResponseEntity<List<Map<String, Object>>> getUserBookshelf(@RequestParam("userId") long userId) throws SQLException {
        	List<Map<String, Object>> stories = BookInformation.getBookShelf(userId);
        	return ResponseEntity.ok(stories);
        }



        @PostMapping("/update")
        public ResponseEntity<String> updateProfile(
                @RequestParam(value = "id") long userId,
                @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                @RequestParam(value = "firstName", required = false) String firstName,
                @RequestParam(value = "lastName", required = false) String lastName,
                @RequestParam(value = "soDienThoaiInput", required = false) String soDienThoai,
                @RequestParam(value = "emailInput", required = false) String email,
                @RequestParam(value = "gioiTinhInput", required = false) String gioiTinh,
                @RequestParam(value = "butDanhInput", required = false) String butDanh,
                @RequestParam(value = "namSinhInput", required = false) Integer namSinh) {

            try {
                User a = UserInformation.getUserInformation(userId);

                // Cập nhật thông tin
                if (butDanh != null) a.setButdanh(butDanh);
                if (email != null) a.setEmail(email);
                if (firstName != null) a.setFirstName(firstName);
                if (gioiTinh != null) a.setGioitinh(gioiTinh);
                if (lastName != null) a.setLastName(lastName);
                if (soDienThoai != null) a.setPhonenumber(soDienThoai);
                if (namSinh != null && namSinh != 0) a.setNamsinh(namSinh);

                
                if (avatar != null && !avatar.isEmpty()) {
                    String cloudinaryUrl = cloudinaryService.uploadFile(avatar);
                    a.setScrA(cloudinaryUrl);
                }

                if (UserInformation.updateUserInformation(a)) {
                    return ResponseEntity.ok("Cập nhật thành công, avatar: " + a.getScrA());
                } else {
                    return ResponseEntity.status(500).body("Cập nhật thất bại");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Lỗi khi upload ảnh");
            }
        }
        @PostMapping("/add-to-bookshelve")
        public ResponseEntity<String> addToBookshelve(@RequestBody Map<String,Object> mp) throws SQLException{
        	int userId = Integer.parseInt(mp.get("userId").toString());
        	int chapterId = Integer.parseInt(mp.get("chapterId").toString());
        	int bookId = Integer.parseInt(mp.get("bookId").toString());
        	System.out.println(chapterId + " " + bookId);
        	boolean up = ChapterInformation.addToBookshelves(userId, chapterId, bookId);
        	if(up) return ResponseEntity.ok("done");
        	else return ResponseEntity.status(500).body("false");
        }
    
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
             

