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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.bean.Chapter;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;

@Controller
public class VietTruyenController {
	@GetMapping("/chapter")
	public static String getChapter() {
		return "chapter (1)";
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
		return "tmp";
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
	private String BASE_UPLOAD_DIR = "F:\\gt\\Jav\\test\\src\\main\\resources\\uploads\\user";
	@ResponseBody
	@PostMapping("/tao-truyen")
	public boolean taoTruyenMoi(@RequestBody Map<String, Object> story) throws FileNotFoundException {
	    // Giải mã các trường dữ liệu từ request body
	    String title = (String) story.get("title");
	    String genres = (String) story.get("genres");
	    String description = (String) story.get("description");
	    String image = (String) story.get("image");
	    int authorId = (int) story.get("author_id");
	    String imageSrc = null;
	    String tloai[] = genres.split(", ");
	    int storyId = BookInformation.generate(title, tloai, description, imageSrc, authorId);
	    if (image != null && image.contains("data:image")) {
	        try {
	            
	            String base64Image = image.split("base64,")[1];
	            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

	            
	            String uploadDirPath = BASE_UPLOAD_DIR + File.separator + "id" + authorId + File.separator + "stories";
	            File uploadDir = new File(uploadDirPath);

	            
	            if (!uploadDir.exists()) {
	                uploadDir.mkdirs();
	            }
	            String fileName = "truyen"+storyId + ".jpg";
	            File outputFile = new File(uploadDir, fileName);

	            
	            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
	                fileOutputStream.write(imageBytes);
	            }
	            imageSrc = "/uploads/user/id" + authorId + "/stories/" + fileName;
	        } catch (IOException e) {
	            e.printStackTrace(); 
	            return false;
	        }
	    } else {
	    	imageSrc = image;
	    }
	    
	    System.out.println(title+" " + genres + " "+ description+ "  "+ imageSrc+" "+authorId+" "+ storyId);
	    BookInformation.updateImageSrc(storyId, imageSrc);
	    if(storyId > 0 )
	    	return true;
	    return false;
	}
	@DeleteMapping("delete-chapter/{id}")
    public ResponseEntity<String> deleteChapter(@PathVariable int id) throws SQLException {
		boolean isDeleted = ChapterInformation.deleteChapterById(id);
		if (isDeleted) {
            return ResponseEntity.ok("Chương đã được xóa thành công.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy chương !");
        }
    }
	@PostMapping("/generate-chapter")
	public ResponseEntity<String> generateChapter(@RequestBody Map<String, Object> mp) throws SQLException{
		String title = (String) mp.get("title");
		String content = (String) mp.get("content");
		int number = Integer.parseInt(mp.get("number").toString());
		int book_id = Integer.parseInt(mp.get("book_id").toString());
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
            @RequestBody Map<String,Object> mp) {

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
	public List<Chapter> getChapters(@PathVariable int id) throws SQLException{
		List<Chapter> chapters = ChapterInformation.getChaptersByBookId(id);
		return chapters;
	}
	@ResponseBody 
	@GetMapping("/chapter-edit/{id}")
	public Chapter getChapter(@PathVariable int id) {
		Chapter c = ChapterInformation.getChapterById(id);
		return c;
	}
}
