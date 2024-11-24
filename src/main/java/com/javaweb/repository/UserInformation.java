package com.javaweb.repository;
import com.javaweb.bean.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
	public static boolean updateUserInformation(User a) {
		
		String sql = "update [auth_user] set email = ? , first_name = ?, last_name = ?, butdanh =?, namsinh =?,gioitinh =?, srcA = ?,phonenumber = ?  where username = ?";
		try(Connection conn = ConnectionDB.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1,a.getEmail());
			stmt.setString(2,a.getFirstName());
			stmt.setString(3,a.getLastName());
			stmt.setString(4,a.getButdanh());
			stmt.setInt(5, a.getNamsinh());
			stmt.setString(6, a.getGioitinh());
			stmt.setString(8,a.getPhonenumber());
			stmt.setString(9, a.getUsername());
			stmt.setString(7,a.getScrA());
			stmt.executeUpdate();
		} catch(SQLException e) {
			
			e.printStackTrace();
			return false;
		}
		return true;
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
}
