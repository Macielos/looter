package pl.looter.appengine.domain;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Arjan on 21.04.2016.
 */
@Data
public class EventParticipation implements Serializable {

    private Long userId;

    private int score;

    public EventParticipation(Long userId) {
        this.userId = userId;
        this.score = 0;
    }

    public void addScore(int score) {
        this.score += score;
    }

}
