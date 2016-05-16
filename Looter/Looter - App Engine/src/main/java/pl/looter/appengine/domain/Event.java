package pl.looter.appengine.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class Event {

	@Id
	Long id;

	@Index
	private Ref<User> master;

	private String title;

	private String description;

	@Index
	private boolean open;

	@Index
	private long date;

	private long startTime;

	private long endTime;

	public Event() {

	}

	public Event(String title, String description, long date, long startTime, long endTime, User master, boolean open) {
		this.title = title;
		this.description = description;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.master = Ref.create(master);
		this.open = open;
	}

	public User getMaster() {
		return master == null ? null : master.get();
	}

	public void setMaster(User master) {
		this.master = Ref.create(master);
	}
}
