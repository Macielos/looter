package pl.looter.appengine.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Entity
@Data
public class EventParticipation {

	public enum Status {REQUESTED, INVITED, REJECTED, ACCEPTED};

	@Id
	Long id;

	@Index
	private Ref<User> participant;
	@Index
	private Ref<User> master;

	private Ref<Event> event;

	@Index
	private long eventTime;

	private long sendTime;

	private long acceptTime;

	@Index
	private Status status;

	private int score;

	private Set<Long> visitedPoints = new HashSet<>();

	private List<Long> foundLoot = new ArrayList<>();

	public EventParticipation() {

	}

	public EventParticipation(User participant, User master, Event event) {
		this.participant = Ref.create(participant);
		this.master = Ref.create(master);
		this.event = Ref.create(event);
		this.eventTime = event.getDate();
		this.sendTime = new Date().getTime();
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
