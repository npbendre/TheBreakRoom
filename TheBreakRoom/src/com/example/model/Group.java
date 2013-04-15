package com.example.model;

import java.util.Date;

public class Group {

	boolean takeoutflag;
	
	public boolean isTakeOutflag() {
		return takeoutflag;
	}

	public void setTakeOutflag(boolean takeoutflag) {
		this.takeoutflag = takeoutflag;
	}

	String name;
	String id;
	Date date;
	int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
