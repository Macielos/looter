package pl.looter.appengine.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

import lombok.Data;

@Entity
@Data
public class Invitation {

	public enum Status {SENT, ACCEPTED, REJECTED};

	@Id
	Long id;

	private Long inviteeId;

	private Long inviterId;

	private Long eventId;
	
	private Date sendTime;
	
	private Status status;

	public Invitation(Long inviteeId, Long inviterId, Long eventId) {
		this.inviteeId = inviteeId;
		this.inviterId = inviterId;
		this.eventId = eventId;
		this.sendTime = new Date();
		this.status = Status.SENT;
	}

}
