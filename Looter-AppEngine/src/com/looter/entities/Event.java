package com.looter.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Event {

	@Id
	private long id;

	private String title;
	
	private Date startTime;

	private Date endTime;
	
	private long masterId;
	
	private long[] participantIds;

	private long[] leaderboard;
	
	private int[] highscores;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getMasterId() {
		return masterId;
	}

	public void setMasterId(long masterId) {
		this.masterId = masterId;
	}

	public long[] getParticipantIds() {
		return participantIds;
	}

	public void setParticipantIds(long[] participantIds) {
		this.participantIds = participantIds;
	}

	public long[] getLeaderboard() {
		return leaderboard;
	}

	public void setLeaderboard(long[] leaderboard) {
		this.leaderboard = leaderboard;
	}

	public int[] getHighscores() {
		return highscores;
	}

	public void setHighscores(int[] highscores) {
		this.highscores = highscores;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
