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

	private Long masterId;

	private String title;

	private String description;

	private boolean open;
	
	private Date startTime;

	private Date endTime;

	public Event() {

	}

	public Event(String title, String description, Date startTime, Date endTime, Long masterId) {
		this.title = title;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.masterId = masterId;
	}

}
