package com.javaweb.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javaweb.bean.Book;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.UserInformation;
import com.sun.mail.iap.Response;
@Controller
public class AdminController {

	@GetMapping("/admin")
	public static String getAdmin(HttpSession session) {
		User user = (User) session.getAttribute("user");
		System.out.println(user);
		if(user!=null) {
			
			if(user.is_supper() == false) {
				
                return "index2";	
			}
			else {
				return "admin (2)";
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
        boolean deleted = UserInformation.deleteUser(id);

        if (deleted) {
            return ResponseEntity.ok("Xóa thành công");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }
    }
	@GetMapping("/admin-home")
	public ResponseEntity<Map<String,Object>> adminhome() throws SQLException{
		int views = BookInformation.getViews();
		int newBooks = BookInformation.getNewBooks();
		int users = UserInformation.getNumUser();
		int reports = ChapterInformation.getReport();
		List<Map<String,Object>> authors = UserInformation.getTop3authors(); 
		Map<String,Object> mp= new HashMap<>();
		mp.put("views_of_today", views);
		mp.put("new_books", newBooks);
		mp.put("total_users", users);
		mp.put("report", reports);
		mp.put("top3_authors", authors);
		return ResponseEntity.ok(mp);
	}
	@GetMapping("/get-data-author")
	public ResponseEntity<Map<String,Object>> get_author(@RequestParam("author_id") int id ) throws SQLException{
		return ResponseEntity.ok(UserInformation.get_author(id));
	}
	@GetMapping("admin/search-users")
	public ResponseEntity<List<User>> searchtUsers(
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email) {
	    
	    List<User> users = UserInformation.searchUsers(name, email);
	    return ResponseEntity.ok(users);
	}
	@GetMapping("admin/search-story")
	public ResponseEntity<List<Book>> searchtStory(
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) String author) {
	    
	    List<Book> Books = BookInformation.searchBooks(title, author);
	    return ResponseEntity.ok(Books);
	}
	@GetMapping("admin/get-truyen-new")
	public ResponseEntity<List<Book>> getTruyenNew() throws SQLException{
		List<Book> books = BookInformation.get_New_Books();
		return ResponseEntity.ok(books);
	}
	@GetMapping("/admin/get-reports")
	public ResponseEntity<List<Map<String,String>>> get_reports() throws SQLException{
		return ResponseEntity.ok(ChapterInformation.get_reports());
	}
}