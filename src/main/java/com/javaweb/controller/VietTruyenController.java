package com.javaweb.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javaweb.bean.Chapter;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.service.CloudinaryService;

@Controller
public class VietTruyenController {
	private final CloudinaryService cloudinaryService;
    @Autowired
    public VietTruyenController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
	@GetMapping("/chapter")
	public static String getChapter() {
		return "chapter";
	}
	@GetMapping("/dangtruyen")
	public static String vietTruyen(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if(user != null)
			return "vt";
		return "loginPlease";
	}
	@GetMapping("/story-details")
	public static String getTruyen(){
		return "story-details";
	}
	
	@GetMapping("/them-truyen")
	public static String getThemTruyen(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if(user != null)
			return "taotruyenmoi";
		return "loginPlease";
	}
	@GetMapping("/chinh-sua-truyen")
	public static String getEdit(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if(user != null)
			return "chinhsuatruyen";
		return "loginPlease";
	}
	@ResponseBody
	@PostMapping("/tao-truyen")
	public boolean taoTruyenMoi(
	        @RequestParam("title") String title,
	        @RequestParam("genres") String genres,
	        @RequestParam("description") String description,
	        @RequestParam(value = "image", required = false) MultipartFile image,
	        @RequestParam("author_id") int authorId,
	        HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		if(user != null)
			return false;
		String imageSrc = "";
	    String[] tloai = genres.split(", ");
	    
	    // Tạo truyện mới trong database (chưa có ảnh)
	    int storyId = BookInformation.generate(title, tloai, description, imageSrc, authorId);

	    if (image != null && !image.isEmpty()) {
	        
	            imageSrc = cloudinaryService.uploadFile(image);
	            
	        
	    }

	
	    BookInformation.updateImageSrc(storyId, imageSrc);

	    return storyId > 0;
	}
	@DeleteMapping("delete-chapter/{id}")
    public ResponseEntity<String> deleteChapter(@PathVariable int id, @RequestParam int id_book,HttpSession session) throws SQLException {
		User user = (User) session.getAttribute("user");
		 if (user == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login to continue.");
		    }
		boolean isDeleted = ChapterInformation.deleteChapterById(id,id_book);
		

		if (isDeleted) {
            return ResponseEntity.ok("Chương đã được xóa thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương !");
        }
    }
	@PostMapping("/generate-chapter")
	public ResponseEntity<String> generateChapter(@RequestBody Map<String, Object> mp,HttpSession session) throws SQLException{
		User user = (User) session.getAttribute("user");
		if(user == null)
			return ResponseEntity.badRequest().body("Please Login to continue");
		String title = (String) mp.get("title");
		String content = (String) mp.get("content");
		int number = Integer.parseInt(mp.get("number").toString());
		int book_id = Integer.parseInt(mp.get("book_id").toString());
		System.out.println(title + " " + number + " " + book_id);
		boolean createChapter = ChapterInformation.createChapter(book_id,title, number, content);
		if(createChapter) {
			BookInformation.updateChapter(book_id, "Create");
			return ResponseEntity.ok("Đã lưu thành công!");
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thành công!");
		}
	}
	@PutMapping("/update-chapter/{id}")
    public ResponseEntity<String> updateChapter(
            @PathVariable("id") int chapterId,
            @RequestBody Map<String,Object> mp,HttpSession session) {
		User user = (User) session.getAttribute("user");
		if(user != null)
			return ResponseEntity.badRequest().body("Please Login to continue");
        // Gọi service để cập nhật chương
        boolean updated = ChapterInformation.updateChapter(chapterId,(String)mp.get("title"),(String)mp.get("content"));
        if (updated) {
            return ResponseEntity.ok("Cập nhật chương thành công!");
        } else {
            return ResponseEntity.status(400).body("Cập nhật chương thất bại!");
        }
    }
	@ResponseBody
	@GetMapping("/chapter-of-book/{id}")
	public List<Chapter> getChapters(@PathVariable int id, @RequestParam int page) throws SQLException{
		List<Chapter> chapters = ChapterInformation.getChaptersByBookId(id,page);
		return chapters;
	}
	@ResponseBody 
	@GetMapping("/chapter-edit/{id}")
	public Chapter getChapter(@PathVariable int id) {
		Chapter c = ChapterInformation.getChapterById(id);
		return c;
	}
}
