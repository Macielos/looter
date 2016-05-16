package pl.looter.app.dataholder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.looter.R;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.domain.eventParticipationApi.model.EventStatus;
import pl.looter.appengine.domain.pointApi.model.Loot;
import pl.looter.appengine.userApi.model.User;
import pl.looter.appengine.userApi.model.UserStatus;

/**
 * Created by Arjan on 30.04.2016.
 */
public class DataHolder {

	private static final DataHolder dataHolder = new DataHolder();
	private static final String TAG = DataHolder.class.getSimpleName();

	private UserStatus userStatus;

	private EventStatusHolder activeEventStatusHolder;

	private Event lastEvent;

	private Set<User> lastEventInvitedUsers;

	private Map<Long, Bitmap> bitmaps = new HashMap<>();

	private List<EventParticipation> invitations;

	private List<EventParticipation> requests;

	public static DataHolder getInstance() {
		return dataHolder;
	}

	public User getUser() {
		return userStatus.getUser();
	}

	public int getNotificationCount() {
		if(userStatus == null) {
			return 0;
		}
		int count = 0;
		if(userStatus.getEventRequestsReceived() != null) {
			count += userStatus.getEventRequestsReceived().size();
		}
		if(userStatus.getEventRequestsAccepted() != null) {
			count += userStatus.getEventRequestsAccepted().size();
		}
		if(userStatus.getInvitationsReceived() != null) {
			count += userStatus.getInvitationsReceived().size();
		}
		if(userStatus.getInvitationsAccepted() != null) {
			count += userStatus.getInvitationsAccepted().size();
		}
		return count;
	}

	public Bitmap getOrCreateBitmap(Loot loot, Context context) {
		Bitmap bitmap = bitmaps.get(loot.getId());
		if(bitmap != null) {
			return bitmap;
		}

		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.class.getField(loot.getImage()).getInt(null));
			if(bitmap != null) {
				bitmaps.put(loot.getId(), bitmap);
				return bitmap;
			}
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "cannot create bitmap - no field "+loot.getImage(), e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "cannot create bitmap - illegal access", e);
		}
		return null;
	}

	public List<EventParticipation> getRequests() {
		return requests;
	}

	public void setRequests(List<EventParticipation> requests) {
		this.requests = requests;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public EventStatusHolder getActiveEventStatusHolder() {
		return activeEventStatusHolder;
	}

	public EventStatus getActiveEventStatus() {
		return activeEventStatusHolder == null ? null : activeEventStatusHolder.getEventStatus();
	}

	public void setActiveEventStatus(EventStatus activeEventStatus) {
		this.activeEventStatusHolder = new EventStatusHolder(activeEventStatus);
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(Event lastEvent) {
		this.lastEvent = lastEvent;
	}

	public Set<User> getLastEventInvitedUsers() {
		return lastEventInvitedUsers;
	}

	public void setLastEventInvitedUsers(Set<User> lastEventInvitedUsers) {
		this.lastEventInvitedUsers = lastEventInvitedUsers;
	}

	public List<EventParticipation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<EventParticipation> invitations) {
		this.invitations = invitations;
	}
}
