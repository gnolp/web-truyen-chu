package com.javaweb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.javaweb.bean.Chapter;

public class ChapterInformation {

	public static void updateChapterContent(int id,String content) {
	    String sql = "UPDATE chapter SET content = ? WHERE id = ?";
	    
	    try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setString(1, content);
	        stmt.setInt(2, id);
	        
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public static Chapter getChapterById(int chapterId) {
		Chapter chapter = null;
		String sql = "SELECT id, title, number, content, id_book FROM chapter WHERE id = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			 stmt.setInt(1, chapterId);
			 ResultSet rs = stmt.executeQuery();
			 if (rs.next()) {
                 
                 chapter = new Chapter();
                 chapter.setId(rs.getInt("id"));
                 chapter.setTitle(rs.getString("title"));
                 chapter.setNumber(rs.getInt("number"));
                 chapter.setContent(rs.getString("content"));  
                 chapter.setId_book(rs.getInt("id_book"));
             }
			 return chapter;
		}catch (SQLException e) {
	        e.printStackTrace();
	    }
		return chapter;
	}
	
	
	public static boolean createChapter(int idBook, String title, int number, String content) {
       
        String sql = "INSERT INTO chapter (title, number, content, id_book) VALUES (?, ?, ?, ?)";
        
        try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
            
           
            stmt.setString(1, title);  
            stmt.setInt(2, number);    
            stmt.setString(3, content); 
            stmt.setInt(4, idBook);    
            
            
            int rowsAffected = stmt.executeUpdate();
            
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();  
        }
        
        return false; 
    }
	
	public static List<Chapter> getChaptersByBookId(int idBook,int page) throws SQLException {
		int size = 2;
		int offset = (page-1)*size;
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT id,title,number FROM chapter WHERE id_book = ? ORDER BY number OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
        	stmt.setInt(1, idBook);
        	stmt.setInt(2, offset);
        	stmt.setInt(3, size);
        	ResultSet rs = stmt.executeQuery();
        	while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int number = rs.getInt("number");
                //String content = rs.getString("content");
                Chapter a = new Chapter();
                //a.setContent(content);
                a.setId_book(idBook);
                a.setNumber(number);
                a.setTitle(title);
                a.setId(id);
                chapters.add(a);
            }
        }
		return chapters;
	}
	public static boolean deleteChapterById(int id, int id_book) throws SQLException {
	    String sqlDelete = "DELETE FROM chapter WHERE id = ?";
	    String sqlUpdate = "UPDATE book SET so_chuong = so_chuong - 1 WHERE id = ?";
	    
	    try (Connection conn = ConnectionDB.getConnection()) {
	        conn.setAutoCommit(false);  // Bắt đầu transaction

	        try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
	            stmtDelete.setInt(1, id);
	            int rowsAffected = stmtDelete.executeUpdate();

	            if (rowsAffected > 0) {
	                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
	                    stmtUpdate.setInt(1, id_book);
	                    stmtUpdate.executeUpdate();
	                }
	                conn.commit();  // Commit khi mọi thứ thành công
	                return true;
	            } else {
	                conn.rollback();  // Rollback nếu không xóa được
	                return false;
	            }
	        } catch (SQLException e) {
	            conn.rollback();  // Rollback nếu có lỗi
	            throw e;
	        } finally {
	            conn.setAutoCommit(true);  // Khôi phục trạng thái mặc định
	        }
	    }
	}

	public static boolean updateChapter(int idChapter, String title, String content) {
	    String sql = "UPDATE chapter SET title = ?, content = ? WHERE id = ?"; 
	    
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        
	        stmt.setString(1, title);   
	        stmt.setString(2, content);   
	        stmt.setInt(3, idChapter); 

	        
	        int rowsAffected = stmt.executeUpdate();

	        
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        e.printStackTrace(); 
	    }

	    return false; 
	}
	public static boolean addToBookshelves(int userID,int chapterId, int bookId) throws SQLException {
		String sql = "MERGE INTO user_book_progress AS target " +
                "USING ( " +
                "    SELECT ? AS current_page, ? AS book_id, " +
                "           (SELECT TOP 1 b.bookshelf_id " +
                "            FROM bookshelves b " +
                "            JOIN auth_user u ON u.id = b.user_id " +
                "            WHERE u.id = ?) AS bookshelf_id " +
                ") AS source " +
                "ON target.book_id = source.book_id AND target.bookshelf_id = source.bookshelf_id " +
                "WHEN MATCHED THEN " +
                "    UPDATE SET target.current_page = source.current_page " +
                "WHEN NOT MATCHED THEN " +
                "    INSERT (current_page, book_id, bookshelf_id) " +
                "    VALUES (source.current_page, source.book_id, source.bookshelf_id);";
		try (Connection conn = ConnectionDB.getConnection();
		         PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, chapterId);
			stmt.setInt(2, bookId);
			stmt.setInt(3, userID);
			int rs = stmt.executeUpdate();
			return rs > 0;
		}
	}
	public static boolean addReport(String content, Integer chapterId) throws SQLException {
		String sql = "insert into report (chapter_id,content) values (?,?)";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, chapterId);
			stmt.setString(2, content);
			int rs = stmt.executeUpdate();
			return rs > 0;
		}
	}
	public static int getReport() throws SQLException {
		String sql ="select count(*) as reports from report where had_seen = 0;";
		try(Connection conn = ConnectionDB.getConnection();
				Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt("reports");
		}
	}
	public static List<Map<String, String>> get_reports() throws SQLException {
	    String sql = "SELECT " +
	                 "    c.id AS chapter_id, " +
	                 "    b.id AS book_id, " +
	                 "    b.title AS book_title, " +
	                 "    r.id AS report_id, " +
	                 "    r.content AS report_content, " +
	                 "    r.had_seen, " +
	                 "    c.number AS chapter_number " +
	                 "FROM report r " +
	                 "JOIN chapter c ON r.chapter_id = c.id " +
	                 "JOIN book b ON c.id_book = b.id where r.had_seen=0;";

	    List<Map<String, String>> reports = new ArrayList<>();

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Map<String, String> mp = new HashMap<>();
	            mp.put("chapter_id", rs.getString("chapter_id"));
	            mp.put("book_id", rs.getString("book_id"));
	            mp.put("book_title", rs.getString("book_title"));
	            mp.put("report_id", rs.getString("report_id"));
	            mp.put("report_content", rs.getString("report_content"));
	            mp.put("had_seen", rs.getString("had_seen"));
	            mp.put("chapter_number", rs.getString("chapter_number"));

	            reports.add(mp);
	        }
	    }

	    return reports;
	}
	public static String getContentById(int id) throws SQLException {
	    String sql = "SELECT content FROM chapter WHERE id = ?";
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("content");
	            } else {
	                return null;
	            }
	        }
	    }
	}
	
	public static Chapter readContinue(int chapterid) throws SQLException {
		String sql = "Select * from chapter where id = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, chapterid);
			ResultSet rs = stmt.executeQuery();
			Chapter chapter = new Chapter();
			while(rs.next()) {
				chapter.setId(rs.getInt("id"));
                chapter.setNumber(rs.getInt("number"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
			}
			return chapter;
		}
	}
	
	public static Chapter getNextChapter(int storyId, int currentChapterId) throws SQLException {
    String sql = "SELECT top 1 * FROM chapter " +
                 "WHERE id_book = ? AND number > (" +
                 "SELECT number FROM chapter WHERE id = ?" +
                 ") ORDER BY number ASC";

    try (Connection conn = ConnectionDB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, storyId);
        stmt.setInt(2, currentChapterId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Chapter chapter = new Chapter();
                chapter.setId(rs.getInt("id"));
                chapter.setNumber(rs.getInt("number"));
                chapter.setTitle(rs.getString("title"));
                chapter.setContent(rs.getString("content"));
                return chapter;
            }
        }
    }
    return null;
}
	public static Chapter getPreviousChapter(int storyId, int currentChapterId) throws SQLException {
	    String sql = "SELECT top 1 * FROM chapter " +
	                 "WHERE id_book = ? AND number < (" +
	                 "SELECT number FROM chapter WHERE id = ?" +
	                 ") ORDER BY number DESC ";

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, storyId);
	        stmt.setInt(2, currentChapterId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                Chapter chapter = new Chapter();
	                chapter.setId(rs.getInt("id"));
	                chapter.setNumber(rs.getInt("number"));
	                chapter.setTitle(rs.getString("title"));
	                chapter.setContent(rs.getString("content"));
	                return chapter;
	            }
	        }
	    }
	    return null;
	}


}
