package com.looter.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Point {

	@Id
	private long id;

	private long eventId;
	
	private double x;
	
	private double y;

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
