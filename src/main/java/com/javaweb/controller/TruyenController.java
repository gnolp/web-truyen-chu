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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javaweb.bean.Book;
import com.javaweb.bean.Category;
import com.javaweb.bean.Chapter;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.UserInformation;
import com.javaweb.service.CloudinaryService;

@RestController
public class TruyenController {
	private final CloudinaryService cloudinaryService;
    @Autowired
    public TruyenController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
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
	public Map<String,Object> getTruyenByIdTheLoai(@PathVariable("id") int id) throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getBookByIdOfKind(id);
		String nameTL = getTl(id);
		mp.put("stories", books);
		mp.put("TheLoai", nameTL);
		mp.put("idtl", id);
		return mp;
		
	}
	@GetMapping("/stories-hot")
	public  Map<String,Object> getTruyenHot() throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getTruyenHot();
		mp.put("stories", books);
		return mp;
	}
	@GetMapping("/TheLoai/{theloai}")
	public  int getIdTheLoai(@PathVariable("theloai") String tl) throws SQLException {
		return BookInformation.getIdTheLoai(tl);
	}
	public  String getTl(int id) throws SQLException {
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
	@PostMapping("/update-truyen")
	public boolean updateStory(
	        @RequestParam("title") String title,
	        @RequestParam("genres") String genres,
	        @RequestParam("description") String description,
	        @RequestParam(value = "image", required = false) MultipartFile image,
	        @RequestParam("status") String status,
	        @RequestParam("author_id") int authorId,
	        @RequestParam("story_id") int storyId) throws IOException {

	    String imageSrc = "";
	    String[] tloai = genres.split(", ");

	    // Nếu có ảnh mới, tải lên Cloudinary
	    if (image != null && !image.isEmpty()) {
	        imageSrc = cloudinaryService.uploadFile(image);
	    }

	    boolean update = BookInformation.updateTruyen(title, tloai, description, imageSrc, authorId, storyId, status);

	    return update;
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
	public Map<String,Object> getTruyenNew() throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getTruyenNew(10);
		mp.put("stories", books);
		return mp;
	}
	@PostMapping("/report")
	public ResponseEntity<String> report(@RequestBody Map<String,Object> mp) throws SQLException{
		String content = mp.get("content").toString();
		int chapterId = Integer.parseInt(mp.get("chapterId").toString());
		boolean status = ChapterInformation.addReport(content,chapterId);
		if(status) return ResponseEntity.ok("Thành công");
		else return ResponseEntity.status(500).body("Không gửi được");
	}
	@PatchMapping("/increase-views")
	public  void increaseViews(@RequestBody Map<String,Object> mp) throws SQLException{
		int book_id = Integer.parseInt(mp.get("bookId").toString());
		int chapter_id = Integer.parseInt(mp.get("chapterId").toString());
		BookInformation.increaseViews(book_id,chapter_id);
		
	}
	@GetMapping("/get/content/{chapter_id}")
	public ResponseEntity<Map<String,String>> getContent(@PathVariable("chapter_id") int id) throws SQLException{
		Map<String,String> mp = new HashMap<>();
		String content = ChapterInformation.getContentById(id);
		mp.put("content", content);
		return ResponseEntity.ok(mp);
	}
}
