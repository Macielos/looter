package pl.looter.appengine.domain;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class Point {

	@Id
	private Long id;

	private Long eventId;

	private boolean isRoot;

	private GeoPt geoPoint;

	private Ref<Message> message;

	private Ref<Loot> loot;

	private List<Long> nextPointIds = new ArrayList<>();

	public Point() {

	}

	public Point(float latitude, float longitude) {
		this(new GeoPt(latitude, longitude));
	}

	public Point(GeoPt geoPoint) {
		this.geoPoint = geoPoint;
	}

	public Message getMessage() {
		return message == null ? null : message.get();
	}

	public void setMessage(Message message) {
		this.message = message == null ? null : Ref.create(message);
	}

	public Loot getLoot() {
		return loot == null ? null : loot.get();
	}

	public void setLoot(Loot loot) {
		this.loot = Ref.create(loot);
	}

	public void addNextPoint(Long id) {
		nextPointIds.add(id);
	}
}
