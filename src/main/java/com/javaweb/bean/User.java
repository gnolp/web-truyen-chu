package com.javaweb.bean;

public class User {
	private int id;
	private String username;
	private String password;
	private String email;
	private String firstName;
	private String lastName;
	private int namsinh;
	private String butdanh;
	private String gioitinh;
	private String phonenumber;
	private String scrA;
	private boolean is_supper;
	public int getNamsinh() {
		return namsinh;
	}
	public void setNamsinh(int namsinh) {
		this.namsinh = namsinh;
	}
	public String getButdanh() {
		return butdanh;
	}
	public void setButdanh(String butdanh) {
		this.butdanh = butdanh;
	}
	public String getGioitinh() {
		return gioitinh;
	}
	public void setGioitinh(String gioitinh) {
		this.gioitinh = gioitinh;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getScrA() {
		return scrA;
	}
	public void setScrA(String scrA) {
		this.scrA = scrA;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public User() {
		
	}
	public int getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean is_supper() {
		return is_supper;
	}
	public void setIs_supper(boolean is_supper) {
		this.is_supper = is_supper;
	}
	@Override
	public String toString() {
		return username +" " + id +" " + password +" " + email +" "+ firstName +" "+lastName+" "+ is_supper;
	}
}
