package pl.looter.appengine.domain;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import lombok.Data;

@Entity
@Data
public class Point {

	/*
	- msg
	- typ
		- ukazanie kolejnego pktu
		- zagadka
		- loot
	 */

	@Id
	private Long id;

	private Long eventId;

	private GeoPt geoPoint;

	private Ref<Message> message;

	private Ref<Point> nextPoint;

	public Point() {

	}

	public Point(GeoPt geoPoint) {
		this.geoPoint = geoPoint;
	}

	public Message getMessage() {
		return message.get();
	}

	public void setMessage(Message message) {
		this.message = Ref.create(message);
	}

	public Point getNextPoint() {
		return nextPoint == null ? null : nextPoint.get();
	}

	public void setNextPoint(Point nextPoint) {
		this.nextPoint = nextPoint == null ? null : Ref.create(nextPoint);
	}
}
