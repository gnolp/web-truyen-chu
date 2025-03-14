package com.javaweb.repository;
import com.javaweb.bean.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UserInformation {

	public static boolean checkLogin(String username, String password) {
		String sql = "SELECT passwordd FROM [auth_user] WHERE username = ?"; 
	       try (Connection conn = ConnectionDB.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            ResultSet rs = stmt.executeQuery();
	            
	            if (rs.next()) {
	                String storedPassword = rs.getString("passwordd");
	                return storedPassword.equals(password);
	            }
	
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        return false;
	   }
	public static User getUserInformation(long requestedUserId){
		User a = null;
		System.out.println(requestedUserId);
		String sql = "select * from [auth_user] where id = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setLong(1, requestedUserId);
				ResultSet rs = stmt.executeQuery();
				if(rs.next() ) {
					a = new User();
					a.setId(rs.getInt("id"));
					a.setEmail(rs.getString("email"));
					a.setPassword(rs.getString("passwordd"));
					a.setUsername(rs.getString("username"));
					a.setFirstName(rs.getString("first_name"));
					a.setLastName(rs.getString("last_name"));
					a.setIs_supper(rs.getBoolean("is_superuser"));
					a.setButdanh(rs.getString("butdanh"));
					a.setGioitinh(rs.getString("gioitinh"));
					a.setPhonenumber(rs.getString("phonenumber"));
					a.setScrA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
					a.setNamsinh(rs.getInt("namsinh"));
				}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return a;
	}
	public static User getUserInformation(String username){
		User a = null;
		String sql = "select * from [auth_user] where username = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setString(1, username);
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next()) {
					a = new User();
					a.setId(rs.getInt("id"));
					a.setEmail(rs.getString("email"));
					a.setPassword(rs.getString("passwordd"));
					a.setUsername(rs.getString("username"));
					a.setFirstName(rs.getString("first_name"));
					a.setLastName(rs.getString("last_name"));
					a.setIs_supper(rs.getBoolean("is_superuser"));
					a.setButdanh(rs.getString("butdanh"));
					a.setGioitinh(rs.getString("gioitinh"));
					a.setPhonenumber(rs.getString("phonenumber"));
					a.setScrA(rs.getString("srcA") != null ? rs.getString("srcA"):null);
					a.setNamsinh(rs.getInt("namsinh"));
				}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return a;
	}
	public static String addUser(String username, String email, String password) {
		String sql = "insert into [auth_user] (username,email,passwordd,is_superuser,is_staff,is_active,dated_joined) VALUES (?, ?, ?, 0, 1, 1, GETDATE())";
		String sqlBookshelf = "INSERT INTO [bookshelves] (user_id) VALUES (?)";
		try (Connection conn =ConnectionDB.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				 PreparedStatement stmtBookshelf = conn.prepareStatement(sqlBookshelf)){
			 	stmt.setString(1, username);
			 	stmt.setString(2,email);
			 	stmt.setString(3,password);
			 	int rowsInserted = stmt.executeUpdate();
			 	if (rowsInserted > 0) {
			 		ResultSet generatedKeys = stmt.getGeneratedKeys();
		            if (generatedKeys.next()) {
		                int userId = generatedKeys.getInt(1);

		                
		                stmtBookshelf.setInt(1, userId);
		                stmtBookshelf.executeUpdate();

		                System.out.println("A new user and bookshelf were inserted successfully!");
		                return "login";
		            }
		        } else {
		            return "error: no rows inserted";
		        }
		}
	    catch (SQLIntegrityConstraintViolationException e) {
	        
	        System.err.println("Error: Duplicate entry for username or email.");
	        return "error: duplicate entry";
	    } catch (SQLException e) {
	        // Bắt lỗi chung về SQL (kết nối thất bại, lỗi khác)
	        System.err.println("SQL Error: " + e.getMessage());
	        return "error: sql exception";
	    }
		return "login";	
	}
	public static List<User> getListUsers(){
		List<User> list = new ArrayList<>();
		String sql = "select * from [auth_user]";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				ResultSet rs = stmt.executeQuery();// lưu kết quả query dưới dạng bảng
				while(rs.next()) { // chuyển đến dòng tiếp thep
					User a = new User();
					a.setCreated_at(rs.getString("dated_joined").split("")[0]);
					a.setId(rs.getInt("id"));
					a.setEmail(rs.getString("email"));
					a.setPassword(rs.getString("passwordd"));
					a.setUsername(rs.getString("username"));
					a.setFirstName(rs.getString("first_name"));
					a.setLastName(rs.getString("last_name"));
					a.setIs_supper(rs.getBoolean("is_superuser"));
					a.setButdanh(rs.getString("butdanh"));
					a.setGioitinh(rs.getString("gioitinh"));
					a.setPhonenumber(rs.getString("phonenumber"));
					a.setScrA(rs.getString("srcA"));
					a.setNamsinh(rs.getInt("namsinh"));
					if(!a.is_supper())
						list.add(a);
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		return list;
	}
	public static void changePassword(String newPassword, String username) {
		System.out.println("change password: " + newPassword +" " + username);
		String sql ="update [auth_user] set passwordd = ? where username = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setString(1, newPassword);
				stmt.setString(2, username);
				stmt.executeUpdate();
				System.out.println("ok");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static boolean changePassword(String newPassword, int id) {
		String sql ="update [auth_user] set passwordd = ? where id = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
				stmt.setString(1, newPassword);
				stmt.setInt(2, id);
				int r = stmt.executeUpdate();
				return r>0;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean updateUserInformation(User a) {
	    StringBuilder sql = new StringBuilder("UPDATE [auth_user] SET ");
	    List<Object> params = new ArrayList<>();

	    if (a.getEmail() != null) {
	        sql.append("email = ?, ");
	        params.add(a.getEmail());
	    }
	    if (a.getFirstName() != null) {
	        sql.append("first_name = ?, ");
	        params.add(a.getFirstName());
	        System.out.println(a.getFirstName());
	    }
	    if (a.getLastName() != null) {
	        sql.append("last_name = ?, ");
	        params.add(a.getLastName());
	    }
	    if (a.getButdanh() != null) {
	        sql.append("butdanh = ?, ");
	        params.add(a.getButdanh());
	    }
	    if ((Integer)a.getNamsinh() != null && a.getNamsinh() != 0) {
	        sql.append("namsinh = ?, ");
	        params.add(a.getNamsinh());
	    }
	    if (a.getGioitinh() != null) {
	        sql.append("gioitinh = ?, ");
	        params.add(a.getGioitinh());
	    }
	    if (a.getScrA() != null) {
	        sql.append("srcA = ?, ");
	        params.add(a.getScrA());
	    }
	    if (a.getPhonenumber() != null) {
	        sql.append("phonenumber = ?, ");
	        params.add(a.getPhonenumber());
	    }

	    // Kiểm tra nếu không có gì để cập nhật
	    if (params.isEmpty()) {
	        return false;
	    }

	    // Loại bỏ dấu phẩy cuối cùng và thêm điều kiện WHERE
	    sql.setLength(sql.length() - 2);
	    sql.append(" WHERE username = ?");
	    params.add(a.getUsername());

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        // Gán tham số động
	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }

	        int rowsUpdated = stmt.executeUpdate();
	        return rowsUpdated > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public static List<User> searchUser(String email) {
	    List<User> userList = new ArrayList<>();
	    String sql = "SELECT * FROM [auth_user] WHERE email = ?";
	    
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, email ); 
	        
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	        	
	            User user = new User();
	            user.setUsername(rs.getString("username"));
	            user.setEmail(rs.getString("email"));
	            System.out.println(user.getUsername());
	            userList.add(user);	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return userList;
	}
	public static String getName(int id) throws SQLException {
		String sql ="select butdanh from auth_user where id = ?;";
		String name = null;
		try (Connection conn = ConnectionDB.getConnection();
		        PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, id );
		        ResultSet rs = stmt.executeQuery();
		        while (rs.next()) {
		        	name = rs.getString("butdanh");
		        }
		}
		 return name;       
	}
	
	public static boolean deleteUser(int id) throws SQLException {
		String sql = "DELETE FROM auth_user WHERE id = ?";
		try (Connection conn = ConnectionDB.getConnection();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			int rowE = stmt.executeUpdate();
			return rowE > 0;
		}
	}
	public static int getNumUser() throws SQLException{
		String sql = "select count(*) as so_user from auth_user";
		try(Connection conn = ConnectionDB.getConnection();
				Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			return rs.getInt("so_user");
		}
	}
	public static  List<Map<String,Object>> getTop3authors() throws SQLException{
		List<Map<String,Object>> list = new ArrayList<>();
		String sql="\r\n"
				+ "SELECT top(3)\r\n"
				+ "    u.id, \r\n"
				+ "    u.srcA, \r\n"
				+ "    u.first_name,\r\n"
				+ "	   u.last_name,\r\n"
				+ "    u.butdanh, \r\n"
				+ "    convert(date,dated_joined) as joined_date, \r\n"
				+ "    COUNT(DISTINCT b.id) AS so_truyen, \r\n"
				+ "    COUNT(c.id) AS so_chuong\r\n"
				+ "FROM auth_user u\r\n"
				+ "LEFT JOIN book b ON u.id = b.author_id\r\n"
				+ "LEFT JOIN chapter c ON c.id_book = b.id\r\n"
				+ "GROUP BY u.id, u.srcA, u.first_name,u.last_name, u.butdanh, u.dated_joined\r\n"
				+ "ORDER BY so_truyen DESC;\r\n"
				+ "";
		try(Connection conn = ConnectionDB.getConnection();
				Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				Map<String,Object> mp = new HashMap<>();
				mp.put("id", rs.getInt("id"));
				mp.put("srcA", rs.getString("srcA"));
				mp.put("name", rs.getString("first_name")+" " + rs.getString("last_name"));
				mp.put("butdanh", rs.getString("butdanh"));
				mp.put("created_at", rs.getString("joined_date"));
				mp.put("so_truyen", rs.getInt("so_truyen"));
				mp.put("so_chuong", rs.getInt("so_chuong"));
				list.add(mp);
			}
		}
		return list;
	}
	public static Map<String, Object> get_author(int author_id) throws SQLException {
	    String sql = "SELECT u.id, u.srcA, u.first_name, u.last_name, u.username, u.email " +
	                 "FROM auth_user u WHERE u.id = ?;";
	    
	    Map<String, Object> mp = new HashMap<>();
	    
	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, author_id);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            mp.put("id", rs.getInt("id"));
	            mp.put("srcA", rs.getString("srcA"));
	            mp.put("first_name", rs.getString("first_name"));
	            mp.put("last_name", rs.getString("last_name"));
	            mp.put("username", rs.getString("username"));
	            mp.put("email", rs.getString("email"));
	            
	            int id = (int) mp.get("id"); 
	            
	            
	            List<Map<String, Object>> books = new ArrayList<>();
	            
	            String sql2 = "SELECT b.srcA, b.id, b.title FROM book b " +
	                          "WHERE b.author_id = ? " +
	                          "AND b.created_at >= DATEADD(DAY, -150, CONVERT(DATETIME, CONVERT(DATE, GETDATE()))) " +
	                          "AND b.created_at < DATEADD(DAY, 1, CONVERT(DATETIME, CONVERT(DATE, GETDATE())));";
	            
	            try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
	                stmt2.setInt(1, id);
	                ResultSet rs2 = stmt2.executeQuery();
	                
	                while (rs2.next()) {
	                    Map<String, Object> book = new HashMap<>();
	                    book.put("image", rs2.getString("srcA"));
	                    book.put("book_id", rs2.getInt("id"));
	                    book.put("title", rs2.getString("title"));
	                    books.add(book);
	                }
	            }
	            
	            mp.put("books", books);  
	        }
	    }
	    
	    return mp;
	}
	public static List<User> searchUsers(String name, String email) {
	    List<User> users = new ArrayList<>();
	    StringBuilder sql = new StringBuilder("SELECT * FROM auth_user WHERE 1=1"); // Tránh lỗi khi nối AND
	    List<Object> params = new ArrayList<>();

	    if (name != null && !name.isEmpty()) {
	        sql.append(" AND CONCAT(first_name, ' ', last_name) LIKE ?");
	        params.add("%" + name + "%");
	    }

	    if (email != null && !email.isEmpty()) {
	        sql.append(" AND email LIKE ?");
	        params.add("%" + email + "%");
	    }

	    try (Connection conn = ConnectionDB.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }

	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            User a = new User();
	            a.setCreated_at(rs.getString("dated_joined").split(" ")[0]);
	            a.setId(rs.getInt("id"));
	            a.setEmail(rs.getString("email"));
	            a.setPassword(rs.getString("passwordd"));
	            a.setUsername(rs.getString("username"));
	            a.setFirstName(rs.getString("first_name"));
	            a.setLastName(rs.getString("last_name"));
	            a.setIs_supper(rs.getBoolean("is_superuser"));
	            a.setButdanh(rs.getString("butdanh"));
	            a.setGioitinh(rs.getString("gioitinh"));
	            a.setPhonenumber(rs.getString("phonenumber"));
	            a.setScrA(rs.getString("srcA"));
	            a.setNamsinh(rs.getInt("namsinh"));

	            if (!a.is_supper()) {
	                users.add(a);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}

}
