package pl.looter.app.dataholder;

import java.util.List;

import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.userApi.model.User;

/**
 * Created by Arjan on 30.04.2016.
 */
public class DataHolder {

	private static final DataHolder dataHolder = new DataHolder();

	private User user;
	private Event lastEvent;
	private List<EventParticipation> invitations;
	private List<EventParticipation> requests;
	private List<EventParticipation> notifications;

	public static DataHolder getInstance() {
		return dataHolder;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(Event eventUnderCreation) {
		this.lastEvent = eventUnderCreation;
	}

	public List<EventParticipation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<EventParticipation> invitations) {
		this.invitations = invitations;
	}

	public List<EventParticipation> getRequests() {
		return requests;
	}

	public void setRequests(List<EventParticipation> requests) {
		this.requests = requests;
	}

	public List<EventParticipation> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<EventParticipation> notifications) {
		this.notifications = notifications;
	}
}
