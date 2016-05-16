package pl.looter.app.interfaces;

import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;

/**
 * Created by Arjan on 23.04.2016.
 */
public interface IOnInvitation {

	void onAccept(EventParticipation invitation);

	void onReject(EventParticipation invitation);
}
