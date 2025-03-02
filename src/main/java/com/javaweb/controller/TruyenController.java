package com.javaweb.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.io.IOException;	
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileOutputStream;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javaweb.bean.Book;
import com.javaweb.bean.Category;
import com.javaweb.bean.Chapter;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.UserInformation;

@RestController
public class TruyenController {

	@GetMapping("/story/{id}")
	public Map<String,Object> getTruyen(@PathVariable("id") int id){
		Map<String,Object> mp = new HashMap<>();
		try {
			Book book = BookInformation.getBookById(id);
			List<Chapter> chapters = ChapterInformation.getChaptersByBookId(id);
			String auth_name = UserInformation.getName((int)book.getAuthor_id());
			String theLoai = BookInformation.getTheLoai(id);
			mp.put("theloai", theLoai);
			mp.put("bút danh", auth_name);
			mp.put("story", book);
			mp.put("Chuong", chapters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mp;
	}
	@GetMapping("/stories/{id}")
	public static Map<String,Object> getTruyenByIdTheLoai(@PathVariable("id") int id) throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getBookByIdOfKind(id);
		String nameTL = getTl(id);
		mp.put("stories", books);
		mp.put("TheLoai", nameTL);
		mp.put("idtl", id);
		return mp;
		
	}
	@GetMapping("/stories-hot")
	public static Map<String,Object> getTruyenHot() throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getTruyenHot();
		mp.put("stories", books);
		return mp;
	}
	@GetMapping("/TheLoai/{theloai}")
	public static int getIdTheLoai(@PathVariable("theloai") String tl) throws SQLException {
		return BookInformation.getIdTheLoai(tl);
	}
	public static String getTl(int id) throws SQLException {
		return BookInformation.getTL(id);
	}
	@DeleteMapping("/delete-story/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable int id) throws SQLException {
        boolean isDeleted = BookInformation.deleteStoryById(id);
        if (isDeleted) {
            return ResponseEntity.ok("Đã xóa truyện có id: " + id);
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Không tìm thấy truyện ");
        }
    }
	@DeleteMapping("/delete-story-from-bookshelf/{storyId}/{userId}")
	public ResponseEntity<String> deleteBook(@PathVariable int storyId, @PathVariable int userId) throws SQLException {
        boolean isDeleted = BookInformation.deleteStoryFromBookshelf(storyId,userId);
        System.out.println("xóa truyện có id là " + storyId + " khỏi tủ sách của người dùng: " +userId);
        if (isDeleted) {
            return ResponseEntity.ok("Đã xóa truyện có id: " + storyId);
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("Không tìm thấy truyện ");
        }
    }
	private String BASE_UPLOAD_DIR = "F:\\gt\\Jav\\test\\src\\main\\resources\\uploads\\user";
	@PostMapping("/update-truyen")
	public boolean updateStory(@RequestBody Map<String, Object> story) throws FileNotFoundException {
	    // Giải mã các trường dữ liệu từ request body
	    String title = (String) story.get("title");
	    String genres = (String) story.get("genres");
	    String description = (String) story.get("description");
	    String image = (String) story.get("image");
	    String status = (String) story.get("status");
	    int authorId = (int) story.get("author_id");
	    int storyId = (int) story.get("story_id");
	    String imageSrc = null;
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
	    String tloai[] = genres.split(", ");
	    System.out.println(title+" " + genres + " "+ description+ "  "+ imageSrc+" "+authorId+" "+ storyId);
	    boolean update = BookInformation.updateTruyen(title, tloai, description, imageSrc, authorId, storyId,status);
	    if(update)
	    	return true;
	    return false;
	}
	
	@GetMapping("/search-stories")
    public ResponseEntity<List<Book>> searchStories(@RequestParam("keyword") String keyword) throws SQLException {
        List<Book> stories = BookInformation.searchStories(keyword);
        for(Book a: stories) {
        	System.out.println(a);
        }
        return ResponseEntity.ok(stories);
        
    }
	@GetMapping("/get-chapter/{id}")
	public Chapter getChapter(@PathVariable int id) {
		return ChapterInformation.getChapterById(id);
	}
	@GetMapping("/category-of-story/{id}")
	public ResponseEntity<List<Category>> getCategory(@PathVariable("id") int book_id) throws SQLException{
		return ResponseEntity.ok(BookInformation.getCategory(book_id));
	}
	@GetMapping("/stories-new")
	public static Map<String,Object> getTruyenNew() throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getTruyenNew(10);
		mp.put("stories", books);
		return mp;
	}
}
