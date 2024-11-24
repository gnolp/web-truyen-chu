package com.javaweb.bean;

import java.sql.Date;
import java.time.LocalTime;

public class Book {
	private int id;
	private String title;
	private long author_id;
	private Date created_at;
	private Date updated_at;
	private int luotdoc;
	private String srcA;
	private int so_chuong;
	private String mo_ta;
	private String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMo_ta() {
		return mo_ta;
	}
	public void setMo_ta(String mo_ta) {
		this.mo_ta = mo_ta;
	}
	public int getSo_chuong() {
		return so_chuong;
	}
	public void setSo_chuong(int so_chuong) {
		this.so_chuong = so_chuong;
	}
	public Book() {
		// TODO Auto-generated constructor stub
	}
	public String getSrcA() {
		return srcA;
	}
	public void setSrcA(String srcA) {
		this.srcA = srcA;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getAuthor_id() {
		return author_id;
	}
	public void setAuthor_id(long author_id) {
		this.author_id = author_id;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date date) {
		this.created_at = date;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date date) {
		this.updated_at = date;
	}
	public int getLuotdoc() {
		return luotdoc;
	}
	public void setLuotdoc(int luotdoc) {
		this.luotdoc = luotdoc;
	}
	@Override
	public String toString() {
		return title + " " +so_chuong; 
	}

}
