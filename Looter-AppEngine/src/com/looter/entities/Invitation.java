package com.looter.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Invitation {
	
	@Id
	private long inviteeId;

	private long eventId;
	
	private long sendTime;
	
	private int status;

	public long getInviteeId() {
		return inviteeId;
	}

	public void setInviteeId(long inviteeId) {
		this.inviteeId = inviteeId;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
