package com.javaweb.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javaweb.bean.Book;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.UserInformation;
@Controller
public class AdminController {

	@GetMapping("/admin")
	public static String getAdmin(HttpSession session) {
		User user = (User) session.getAttribute("user");
		System.out.println(user);
		if(user!=null) {
			
			if(user.is_supper() == false) {
				
                return "index (1)";	
			}
			else {
				return "admin";
			}
		}
		return "loginPlease";
	}
	@ResponseBody
	@GetMapping("/get-users")
	public List<User> getUsers(){
		List<User> users = UserInformation.getListUsers();
		return users;
	}
	@ResponseBody
	@GetMapping("/get-books")
	public List<Book> getBooks() throws SQLException{
		List<Book> books = BookInformation.getAllBook();
		return books;
	}
	@DeleteMapping("deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) throws SQLException {
        // Logic xóa người dùng từ cơ sở dữ liệu
        boolean deleted = UserInformation.deleteUser(id);

        if (deleted) {
            return ResponseEntity.ok("Xóa thành công");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }
    }
}
