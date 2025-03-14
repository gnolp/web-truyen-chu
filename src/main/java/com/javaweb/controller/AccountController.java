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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.google.api.client.util.Value;
import com.javaweb.bean.Book;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.UserInformation;
import com.sun.mail.iap.Response;
@Controller
public class AccountController {
    

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

        private String BASE_UPLOAD_DIR = "F:\\gt\\Jav\\test\\src\\main\\resources\\uploads\\user";


        @PostMapping("/update")
        public ResponseEntity<String> updateProfile(
        		@RequestParam(value ="id") long userId,
                @RequestParam(value ="avatar", required = false) MultipartFile avatar,
                @RequestParam(value ="firstName", required = false) String firstName,
                @RequestParam(value ="lastName", required = false) String lastName,
                @RequestParam(value ="soDienThoaiInput", required = false) String soDienThoai,
                @RequestParam(value ="emailInput", required = false) String email,
                @RequestParam(value ="gioiTinhInput", required = false) String gioiTinh,
                @RequestParam(value ="butDanhInput", required = false) String butDanh,
                @RequestParam(value ="namSinhInput", required = false) Integer namSinh) {
        		
        	

            
            String uploadDirPath = BASE_UPLOAD_DIR + File.separator + "id" + userId + File.separator + "avt";
            
            	System.out.println("dòng 92 accountController");
            	try {
            	    Path path = null;
            	    if (avatar != null && !avatar.isEmpty()) {
            	        File uploadDir = new File(uploadDirPath);
            	        if (!uploadDir.exists()) {
            	            uploadDir.mkdirs();
            	        }

            	        // Tạo file để lưu
            	        String fileName = "avatar_" + userId + ".png";
            	        path = Path.of(uploadDirPath + File.separator + fileName);
            	        Files.copy(avatar.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            	    }

            	    // Lưu thông tin người dùng
            	    System.out.println("id: " + userId);
            	    System.out.println("Avatar saved at: " + path);
            	    System.out.println("Tên hiển thị: " + firstName + " " + lastName);
            	    System.out.println("Số điện thoại: " + soDienThoai);
            	    System.out.println("Email: " + email);
            	    System.out.println("Giới tính: " + gioiTinh);
            	    System.out.println("Bút danh: " + butDanh);
            	    
					System.out.println("Năm sinh: " + namSinh);

            	    User a = UserInformation.getUserInformation(userId);
            	    if (butDanh != null) a.setButdanh(butDanh);
            	    if (email != null) a.setEmail(email);
            	    if (firstName != null) a.setFirstName(firstName);
            	    if (gioiTinh != null) a.setGioitinh(gioiTinh);
            	    if (lastName != null) a.setLastName(lastName);
            	    if (soDienThoai != null) a.setPhonenumber(soDienThoai);
            	    if (namSinh != null && namSinh != 0) a.setNamsinh(namSinh);

            	    // Cập nhật đường dẫn ảnh (nếu có)
            	    if (path != null) {
            	        a.setScrA(path.toString().substring(33));
            	    }

            	    // Cập nhật thông tin người dùng
            	    if (UserInformation.updateUserInformation(a)) {
            	    	System.out.println("ok");
            	        return ResponseEntity.ok("Cập nhật thành công");
            	    } else {
            	    	System.out.println("sai ở đây");
            	        return ResponseEntity.status(500).body("Cập nhật thất bại");
            	    }
            	} catch (IOException e) {
            	    // Log lỗi chi tiết
            		System.out.println("sai ở đây2");
            	    e.printStackTrace();
            	    return ResponseEntity.status(500).body("Lỗi khi lưu ảnh");
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
        
}
             

