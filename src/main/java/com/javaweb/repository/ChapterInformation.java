package com.javaweb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Chapter> getChaptersByBookId(int idBook) throws SQLException {
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM chapter WHERE id_book = ? ORDER BY number";
        try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
        	stmt.setInt(1, idBook);
        	ResultSet rs = stmt.executeQuery();
        	while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int number = rs.getInt("number");
                String content = rs.getString("content");
                Chapter a = new Chapter();
                a.setContent(content);
                a.setId_book(idBook);
                a.setNumber(number);
                a.setTitle(title);
                a.setId(id);
                chapters.add(a);
            }
        }
		return chapters;
	}
	public static boolean deleteChapterById(int id) throws SQLException {
		String sql = "Delete from chapter where id = ?";
		 try(Connection conn = ConnectionDB.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql)){
			 stmt.setInt(1, id);
			 int rs = stmt.executeUpdate();
			 return rs > 0;
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
}
