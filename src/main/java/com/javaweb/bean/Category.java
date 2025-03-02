package com.javaweb.bean;

public class Category {

	private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Category() {
		// TODO Auto-generated constructor stub
	}
	public int getId() { return id; }
    public String getName() { return name; }
}
