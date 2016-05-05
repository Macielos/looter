package pl.looter.appengine.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

import lombok.Data;

@Entity
@Data
public class EventParticipation {

	public enum Status {REQUESTED, INVITED, REJECTED, ACCEPTED};

	@Id
	Long id;

	private Ref<User> participant;

	private Ref<User> master;

	private Ref<Event> event;

	private Date sendTime;
	
	private Status status;

	private int score;

	public EventParticipation() {

	}

	public EventParticipation(User participant, User master, Event event) {
		this.participant = Ref.create(participant);
		this.master = Ref.create(master);
		this.event = Ref.create(event);
		this.sendTime = new Date();
		this.status = Status.INVITED;
		this.score = 0;
	}

	public User getParticipant() {
		return participant.get();
	}

	public void setParticipant(User participant) {
		this.participant = Ref.create(participant);
	}

	public User getMaster() {
		return master.get();
	}

	public void setMaster(User master) {
		this.master = Ref.create(master);
	}

	public Event getEvent() {
		return event.get();
	}

	public void setEvent(Event event) {
		this.event = Ref.create(event);
	}
}
