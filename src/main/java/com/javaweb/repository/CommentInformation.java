package com.javaweb.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.javaweb.bean.Comment;

public class CommentInformation {
	public static List<Comment> getCommentByIdOfBook(int book_id, int page) {
	    int size = 10;
	    int offset = (page - 1) * size;
	    String sql = "SELECT c.id, c.user_id, u.first_name, u.last_name, c.content, c.created_at " +
	                 "FROM comment c JOIN auth_user u ON c.user_id = u.id " +
	                 "WHERE c.book_id = ? ORDER BY c.created_at DESC " +
	                 "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

	    List<Comment> cmts = new ArrayList<>();

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, book_id);
	        stmt.setInt(2, offset);
	        stmt.setInt(3, size);

	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            Comment cmt = new Comment();
	            cmt.setId(rs.getInt("id"));
	            cmt.setAuthor(rs.getInt("user_id"));
	            cmt.setAuthor_name(rs.getString("first_name") + " " +rs.getString("last_name"));
	            cmt.setContent(rs.getString("content"));
	            cmt.setCreateAt(rs.getTimestamp("created_at").toLocalDateTime());
	            cmt.setCanEdit(false); 
	            cmts.add(cmt);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return cmts;
	}

	public static Comment comment(int book_id, int author_id, String content,String name) {
	    String sql = "INSERT INTO comment (book_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setInt(1, book_id);
	        stmt.setInt(2, author_id);
	        stmt.setString(3, content);
	        stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
	        int affectedRows = stmt.executeUpdate();

	        if (affectedRows > 0) {
	            ResultSet rs = stmt.getGeneratedKeys();
	            if (rs.next()) {
	                int commentId = rs.getInt(1);
	                // Trả về thông tin bình luận vừa thêm
	                Comment cmt = new Comment();
	                cmt.setId(commentId);
	                cmt.setAuthor(author_id);
	                cmt.setAuthor_name(name); 
	                cmt.setContent(content);
	                cmt.setCreateAt(LocalDateTime.now());
	                cmt.setCanEdit(true);
	                return cmt;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static boolean check(int userId, int cmtId) {
	    String sql = "SELECT id FROM comment WHERE id = ? AND user_id = ?";
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, cmtId);
	        stmt.setInt(2, userId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            return rs.next(); 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); 
	    }
	    return false;
	}

	public static boolean updateContent(int cmtId, String content) {
	    String sql = "UPDATE comment SET content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, content);
	        stmt.setInt(2, cmtId);

	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	public static boolean delete(int cmtId) {
	    String sql = "DELETE FROM comment WHERE id = ?";
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, cmtId);
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

}
