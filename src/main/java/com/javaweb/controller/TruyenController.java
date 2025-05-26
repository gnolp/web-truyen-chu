package com.javaweb.controller;


import java.sql.SQLException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.javaweb.bean.Book;
import com.javaweb.bean.Category;
import com.javaweb.bean.Chapter;
import com.javaweb.bean.Comment;
import com.javaweb.bean.User;
import com.javaweb.repository.BookInformation;
import com.javaweb.repository.ChapterInformation;
import com.javaweb.repository.CommentInformation;
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
	public Map<String,Object> getTruyen(@PathVariable("id") int id , @RequestParam(name = "page", required = false, defaultValue = "1") int page){
		Map<String,Object> mp = new HashMap<>();
		try {
			Book book = BookInformation.getBookById(id);
			List<Chapter> chapters = ChapterInformation.getChaptersByBookId(id,page);
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
	public Map<String,Object> getTruyenByIdTheLoai(@PathVariable("id") int id, @RequestParam int page) throws SQLException{
		Map<String,Object> mp = new HashMap<>();
		List<Book> books = BookInformation.getBookByIdOfKind(id,page);
		String nameTL = getTl(id);
		mp.put("stories", books);
		if(page<=1) {
			mp.put("TheLoai", nameTL);
			mp.put("idtl", id);
		}
		return mp;
		
	}
	@GetMapping("/count-stories-of-category/{id}")
	public int countStories(@PathVariable("id") int id) throws SQLException {
		return BookInformation.countBookByCategory(id);
	}
	
	@GetMapping("/get-chapter-of-story/{id}")
	public List<Chapter> getChapterOfStory(@PathVariable("id") int id, @RequestParam int page) throws SQLException{
		System.out.println(page);
		return ChapterInformation.getChaptersByBookId(id,page);
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
	@PostMapping("/like/{id}/{userId}")
	public ResponseEntity<?> like(@PathVariable int id,@PathVariable int userId, HttpSession session) throws SQLException{
		User user = (User) session.getAttribute("user");
		return BookInformation.addLike(id, userId);
		
	}
	@GetMapping("/get-truyen-liked/{id}")
	public ResponseEntity<?> getLikedStory(@PathVariable int id, @RequestParam(name = "page", required = false, defaultValue = "1") int page){
		return BookInformation.getLikedStory(id,page);
	}
	
	@DeleteMapping("/unlike/{id}/{userId}")
	public ResponseEntity<?> unlikeStory(@PathVariable int id,@PathVariable int userId) throws SQLException{
		return BookInformation.unlike(id,userId);
	}
	
	@GetMapping("/count-book")
	public ResponseEntity<?> countBook(){
		return BookInformation.countAllBook();
	}
	@DeleteMapping("/delete-story-from-bookshelf/{storyId}")
	public ResponseEntity<?> deleteBook(@PathVariable int storyId, HttpSession session) throws SQLException {
		User user = (User) session.getAttribute("user");
		if(user == null)
			return ResponseEntity
	                .status(HttpStatus.SC_FORBIDDEN).body("Vui lòng đăng nhập để tiếp tục!");

        boolean isDeleted = BookInformation.deleteStoryFromBookshelf(storyId,user.getId());
        System.out.println("xóa truyện có id là " + storyId + " khỏi tủ sách của người dùng: " +user.getId());
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
	public Chapter getChapter(
	    @PathVariable int id,
	    @RequestParam int idCurrentChapter,
	    @RequestParam String direction
	) throws SQLException {
	    if (direction.equals("next")) {
	        return ChapterInformation.getNextChapter(id, idCurrentChapter);
	    } else if (direction.equals("prev")) {
	        return ChapterInformation.getPreviousChapter(id, idCurrentChapter);
	    } else {
	        throw new IllegalArgumentException("Direction không hợp lệ.");
	    }
	}
	@GetMapping("/continue-read/{id}")
	public Chapter readContinute(@PathVariable("id") int chapterId) throws SQLException {
		return ChapterInformation.readContinue(chapterId);
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
	@GetMapping("/write-continue/{book_id}")
	public ResponseEntity<?> getNextNumber(@PathVariable int book_id) throws SQLException{
		return ResponseEntity.ok(BookInformation.getNumBer(book_id));
	}
	@GetMapping("/get-comment/{id_book}")
	public ResponseEntity<?> getCommentOfStory(
	        @PathVariable("id_book") int book_id,
	        @RequestParam int page,
	        HttpSession session 
	) {
		User user = (User) session.getAttribute("user");
		int currentUser;
		if(user!=null) {
			currentUser = user.getId();
		}
		else currentUser = -1;
	    try {
	        List<Comment> cmts = CommentInformation.getCommentByIdOfBook(book_id, page);

	        for (Comment cmt : cmts) {
	            if (cmt.getAuthor()==currentUser) {
	                cmt.setCanEdit(true);
	            }
	        }

	        boolean hasMore = cmts.size() == 10; 

	        Map<String, Object> response = new HashMap<>();
	        response.put("comments", cmts);
	        response.put("has_more", hasMore);
	        response.put("next_page", page + 1);

	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
	    }
	}
	@PostMapping("/comment")
	public ResponseEntity<?> comment(
	        @RequestParam int book_id,
	        HttpSession session,
	        @RequestBody Map<String, String> mp) {

	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).body(Map.of("message", "Đăng nhập để tiếp tục!"));
	    }

	    String content = mp.get("content");
	    Comment comment = CommentInformation.comment(book_id, user.getId(), content,user.getFirstName() + user.getLastName());

	    if (comment != null) {
	        return ResponseEntity.ok(Map.of("comment", comment));
	    } else {
	        return ResponseEntity.badRequest().body(Map.of("message", "Có lỗi xảy ra!"));
	    }
	}
	@PutMapping("/edit-comment/{cmtId}")
	public ResponseEntity<?> updateComment(@PathVariable int cmtId, 
	                                       @RequestBody Map<String, String> payload, 
	                                       HttpSession session) {
	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
	            .body(Map.of("message", "Đăng nhập để tiếp tục!"));
	    }

	    if (!CommentInformation.check(user.getId(), cmtId)) {
	        return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
	            .body(Map.of("message", "Không có quyền!"));
	    }

	    String newContent = payload.get("content");
	    if (newContent == null || newContent.trim().isEmpty()) {
	        return ResponseEntity.badRequest()
	            .body(Map.of("message", "Nội dung bình luận không được để trống."));
	    }

	    try {
	        // Giả sử có hàm cập nhật nội dung bình luận trong CommentInformation hoặc service
	        boolean updated = CommentInformation.updateContent(cmtId, newContent.trim());

	        if (updated) {
	            return ResponseEntity.ok(Map.of("message", "Cập nhật bình luận thành công."));
	        } else {
	            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	                .body(Map.of("message", "Cập nhật bình luận thất bại."));
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	            .body(Map.of("message", "Lỗi hệ thống khi cập nhật bình luận."));
	    }
	}
	@DeleteMapping("/delete-comment/{cmtId}")
	public ResponseEntity<?> deleteComment(@PathVariable int cmtId, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
	            .body(Map.of("message", "Bạn cần đăng nhập để xoá bình luận."));
	    }

	    if (!CommentInformation.check(user.getId(), cmtId)) {
	        return ResponseEntity.status(HttpStatus.SC_FORBIDDEN)
	            .body(Map.of("message", "Bạn không có quyền xoá bình luận này."));
	    }

	    boolean success = CommentInformation.delete(cmtId);
	    if (success) {
	        return ResponseEntity.ok(Map.of("message", "Xoá bình luận thành công."));
	    } else {
	        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
	            .body(Map.of("message", "Xoá bình luận thất bại."));
	    }
	}

}