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

import com.javaweb.bean.Book;
import com.javaweb.bean.Chapter;

public class BookInformation {
	
	public static void updateChapter(int id, String active) throws SQLException {
		String sql = null;
		if(active.equals("Delete")) {
			sql = "UPDATE book\r\n"
					+ "SET so_chuong = so_chuong - 1\r\n"
					+ "WHERE id = ? AND so_chuong > 0;";
		}
		else if(active.equals("Create")) {
			sql = "UPDATE book\r\n"
					+ "SET so_chuong = so_chuong + 1\r\n"
					+ "WHERE id = ?;\r\n";
		}
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,id);
			stmt.executeUpdate();
			
		}
	}
	
	public static List<Book> getstories (long userId) throws SQLException{
		List<Book> a = new ArrayList<>();
		
		String sql = "select * from [book] where author_id =?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setLong(1,userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Book x = new Book();
				x.setStatus(rs.getString("status"));
				x.setTitle(rs.getString("title"));
				x.setAuthor_id(rs.getLong("author_id"));
				x.setCreated_at(rs.getDate("created_at"));
				x.setId(rs.getInt("id"));
				x.setLuotdoc(rs.getInt("luot_doc"));
				x.setUpdated_at(rs.getDate("updated_at"));
				x.setSrcA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
				
				List<Chapter> so_chuong = ChapterInformation.getChaptersByBookId(x.getId());
				x.setSo_chuong(so_chuong.size());
				
				System.out.println(x);
				a.add(x);
			}
		}
		
		return a;
	}
	public static List<Map<String, Object>> getBookShelf(long userId) throws SQLException {
		String sql = "SELECT \r\n"
				+ "    b.id AS id,\r\n"
				+ "    b.title AS book_title,\r\n"
				+ "    b.srcA AS book_image,\r\n"
				+ "    a.first_name AS first_name_author,\r\n"
				+ "    a.last_name AS last_name_author,\r\n"
				+ "    c.number AS number,\r\n"
				+ "	   ubp.current_page as chap_id,\r\n"
				+ "	   c.title as chapter_title\r\n"
				+ "FROM \r\n"
				+ "    bookshelves bs\r\n"
				+ "JOIN \r\n"
				+ "    user_book_progress ubp ON bs.bookshelf_id = ubp.bookshelf_id\r\n"
				+ "JOIN \r\n"
				+ "    book b ON ubp.book_id = b.id\r\n"
				+ "JOIN \r\n"
				+ "    auth_user a ON b.author_id = a.id\r\n"
				+ "Join chapter c on c.id = ubp.current_page\r\n"
				+ "WHERE \r\n"
				+ "    bs.user_id = ?;";
		List<Map<String, Object>> list = new ArrayList<>();
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setLong(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				Map<String, Object> mp = new HashMap<>();
				mp.put("id", rs.getInt("id"));
				mp.put("title", rs.getString("book_title"));
				mp.put("srcA", rs.getString("book_image"));
				mp.put("name", rs.getString("first_name_author") + " " + rs.getString("last_name_author"));
				mp.put("chap", rs.getInt("number"));
				mp.put("chapterId", rs.getInt("chap_id"));
				mp.put("chapterTitle",rs.getString("chapter_title"));
				System.out.println(mp.get("chap"));
				list.add(mp);
			}
		}
		return list;
	}
	public static void increaseViews(int id) throws SQLException {
		String sql = "UPDATE Book\r\n"
				+ "SET luot_doc = luot_doc + 1\r\n"
				+ "WHERE id = ?;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}
	public static Book getBookById(int id) {
		String sql = "SELECT * FROM book WHERE id = ?";
		Book book = null;
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				book = new Book();
				book.setStatus(rs.getString("status"));
				book.setAuthor_id(rs.getLong("author_id"));
				book.setCreated_at(rs.getDate("created_at"));
				book.setId(rs.getInt("id"));
				book.setLuotdoc(rs.getInt("luot_doc"));
				book.setMo_ta(rs.getString("mo_ta"));
				book.setSo_chuong(rs.getInt("so_chuong"));
				book.setTitle(rs.getString("title"));
				book.setSrcA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
				book.setUpdated_at(rs.getDate("updated_at"));
			}
			increaseViews(id);
			return book;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return book;
	}
	public static List<Book> getBookByIdOfKind(int id) throws SQLException{
		List<Book> books = new ArrayList<>();
		String sql = "SELECT b.*\r\n"
				+ "FROM book b\r\n"
				+ "JOIN book_category bc ON b.id = bc.book_id\r\n"
				+ "JOIN category c ON c.id = bc.category_id\r\n"
				+ "WHERE c.id = ?;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Book book = new Book();
				book.setStatus(rs.getString("status"));
				book.setAuthor_id(rs.getLong("author_id"));
				book.setCreated_at(rs.getDate("created_at"));
				book.setId(rs.getInt("id"));
				book.setLuotdoc(rs.getInt("luot_doc"));
				book.setMo_ta(rs.getString("mo_ta"));
				book.setSo_chuong(rs.getInt("so_chuong"));
				book.setTitle(rs.getString("title"));
				book.setSrcA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
				book.setUpdated_at(rs.getDate("updated_at"));
				books.add(book);
			}
			return books;
		}
		
	}
	
	public static List<Book> getTruyenHot() throws SQLException{
		List<Book> books = new ArrayList<>();
		String sql = "SELECT TOP 10 *\r\n"
				+ "FROM book\r\n"
				+ "ORDER BY luot_doc DESC;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Book book = new Book();
				book.setStatus(rs.getString("status")+", hot");
				book.setAuthor_id(rs.getLong("author_id"));
				book.setCreated_at(rs.getDate("created_at"));
				book.setId(rs.getInt("id"));
				book.setLuotdoc(rs.getInt("luot_doc"));
				book.setMo_ta(rs.getString("mo_ta"));
				book.setSo_chuong(rs.getInt("so_chuong"));
				book.setTitle(rs.getString("title"));
				book.setSrcA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
				book.setUpdated_at(rs.getDate("updated_at"));
				books.add(book);
			}
			return books;
		}
	}
	public static int getIdTheLoai(String name) throws SQLException{
		if(name.endsWith(",")) {
			name = name.substring(0,name.length()-1);
		}
		String sql = "select id from category where name = ?";
		System.out.println(name);
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1,name);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return rs.getInt("id");
			}
		}
		return -1;
	}
	public static String getTL(int id) throws SQLException{
		String sql = "select name from category where id = ?";
		String tl = null;
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return rs.getString("name");
			}
		}
		return tl;
	}
	public static boolean deleteStoryById(int id) throws SQLException {
		String sql = "DELETE FROM book WHERE id = ?;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,id);
			int rs = stmt.executeUpdate();
			return rs>0;
		}
	}
	public static boolean deleteStoryFromBookshelf(int id, int userId) throws SQLException{
		String sql = "DELETE ubp\r\n"
				+ "FROM user_book_progress ubp\r\n"
				+ "JOIN bookshelves bs ON ubp.bookshelf_id = bs.bookshelf_id\r\n"
				+ "JOIN auth_user u ON bs.user_id = u.id\r\n"
				+ "WHERE ubp.book_id = ? AND u.id = ?;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,id);
			stmt.setInt(2, userId);
			int rs = stmt.executeUpdate();
			return rs>0;
		}
	}
	public static String getTheLoai(int id) throws SQLException{
		String s = "";
		String sql ="SELECT c.name\r\n"
				+ "FROM category c\r\n"
				+ "JOIN book_category bc ON c.id = bc.category_id\r\n"
				+ "JOIN book b ON bc.book_id= b.id\r\n"
				+ "WHERE b.id = ?;";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				s += rs.getString("name");
				s+=",";
			}
		}
		if(s.equals("")) s+= ",";
		return s.substring(0,s.length()-1);
	}
	
	public static boolean updateTruyen(String title, String[] genres,String description,String imageSrc, int author_id, int book_id,String status) {
		String updateBookSql = "UPDATE book SET title = ?, mo_ta = ?, srcA = ? ,status = ? WHERE id = ?";
	    String deleteCategoriesSql = "DELETE FROM book_category WHERE book_id = ?";
	    String insertCategorySql = "INSERT INTO book_category (book_id, category_id) VALUES (?, ?)";
	    try (Connection conn = ConnectionDB.getConnection()) {
	        conn.setAutoCommit(false);

	        
	        try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
	            updateStmt.setString(1, title);
	            updateStmt.setString(2, description);
	            updateStmt.setString(3, imageSrc);
	            updateStmt.setString(4,status);
	            updateStmt.setInt(5, book_id);
	            updateStmt.executeUpdate();
	        }

	        // Xóa thể loại cũ của truyện
	        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCategoriesSql)) {
	            deleteStmt.setInt(1, book_id);
	            deleteStmt.executeUpdate();
	        }

	        // Thêm thể loại mới
	        try (PreparedStatement insertStmt = conn.prepareStatement(insertCategorySql)) {
	            for (String genre : genres) {
	                int categoryId = getIdTheLoai(genre);
	                if (categoryId != -1) {
	                    insertStmt.setInt(1, book_id);
	                    insertStmt.setInt(2, categoryId);
	                    insertStmt.addBatch();
	                }
	            }
	            insertStmt.executeBatch();
	        }

	        conn.commit(); // Commit transaction
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static int generate(String title, String[] genres, String description, String imageSrc, int authorId) {

	    String sql = "INSERT INTO book (title, mo_ta, srcA, author_id,status) VALUES (?, ?, ?, ?,?)";
	    String insertCategorySQL = "INSERT INTO book_category (book_id, category_id) VALUES (?, ?)";

	    int storyId = -1;

	    try (Connection conn = ConnectionDB.getConnection()) {

	        // Chèn vào bảng book
	        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setString(1, title);
	            stmt.setString(2, description);
	            stmt.setString(3, imageSrc);
	            stmt.setInt(4, authorId);
	            stmt.setString(5,"Còn tiếp");

	            int affectedRows = stmt.executeUpdate();

	            // Kiểm tra và lấy storyId từ khóa tự động sinh ra
	            if (affectedRows > 0) {
	                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                    if (generatedKeys.next()) {
	                        storyId = generatedKeys.getInt(1);  // Lấy ID vừa sinh ra
	                    }
	                }
	            }
	        }

	        // Nếu storyId đã được sinh ra, tiến hành chèn vào bảng book_category
	        if (storyId > 0) {
	            try (PreparedStatement insertStmt = conn.prepareStatement(insertCategorySQL)) {
	                for (String genre : genres) {
	                    int categoryId = getIdTheLoai(genre);  // Lấy ID thể loại
	                    if (categoryId != -1) {  // Nếu ID hợp lệ
	                        insertStmt.setInt(1, storyId);   // Set book_id
	                        insertStmt.setInt(2, categoryId); // Set category_id
	                        insertStmt.addBatch();            // Thêm vào batch
	                    }
	                }
	                // Thực hiện batch insert
	                insertStmt.executeBatch();
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return storyId;
	}
	
	public static void updateImageSrc(int bookId, String newImageSrc) {
	    String sql = "UPDATE book SET srcA = ? WHERE id = ?";

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        // Set các tham số trong câu lệnh SQL
	        stmt.setString(1, newImageSrc);  // Đường dẫn ảnh mới
	        stmt.setInt(2, bookId);          // ID của sách

	        // Thực thi câu lệnh cập nhật
	        stmt.executeUpdate();

	        // Nếu có ít nhất một hàng bị ảnh hưởng, tức là cập nhật thành công
	        

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	public static List<Book> getAllBook() throws SQLException{
		List<Book> books = new ArrayList<>();
		String sql = "select title, id,srcA from book;";
		try (Connection conn = ConnectionDB.getConnection();
		         PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Book a = new Book();
				a.setTitle(rs.getString("title"));
				a.setId(rs.getInt("id"));
				a.setSrcA(rs.getString("srcA"));
				books.add(a);
			}
		}
		return books;
	}
	public static List<Book> searchStories(String keysword) throws SQLException{
		System.out.println(keysword);
		List<Book> list = new ArrayList<>();
		String sql = "SELECT *\r\n"
				+ "FROM book\r\n"
				+ "WHERE title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?;";
		try (Connection conn = ConnectionDB.getConnection();
		         PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + keysword + "%");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Book a = new Book();
				a.setAuthor_id(rs.getLong("author_id"));
				a.setId(rs.getInt("id"));
				a.setSrcA(rs.getString("srcA"));
				a.setSo_chuong(rs.getInt("so_chuong"));
				a.setStatus(rs.getString("status"));
				a.setTitle(rs.getString("title"));
				list.add(a);
			}
		}
		return list;
	}
}
