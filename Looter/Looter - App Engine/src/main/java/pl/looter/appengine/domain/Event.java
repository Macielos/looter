package pl.looter.appengine.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class Event {

	@Id
	Long id;

	private Ref<User> master;

	private String title;

	private String description;

	private boolean open;
	
	private Date startTime;

	private Date endTime;

	public Event() {

	}

	public Event(String title, String description, Date startTime, Date endTime, User master) {
		this.title = title;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.master = Ref.create(master);
	}

	public User getMaster() {
		return master.get();
	}

	public void setMaster(User master) {
		this.master = Ref.create(master);
	}
}
