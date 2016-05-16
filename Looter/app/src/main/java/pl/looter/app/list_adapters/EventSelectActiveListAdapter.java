package pl.looter.app.list_adapters;

import android.app.Activity;
import android.view.View;

import java.util.List;

import pl.looter.R;
import pl.looter.app.interfaces.IOnSelect;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.utils.GameplayUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public class EventSelectActiveListAdapter extends AbstractEventParticipationExpandableListAdapter {

	private final IOnSelect<EventParticipation> onSelect;

	public EventSelectActiveListAdapter(List<EventParticipation> eventParticipations, Activity activity, IOnSelect<EventParticipation> onSelect) {
		super(eventParticipations, activity);
		this.onSelect = onSelect;
	}

	@Override
	protected void fillGroupViewInternal(EventParticipation eventParticipation, GroupViewHolder holder) {
		holder.eventItemSubtitle.setText(GameplayUtils.getEventStatusSubtitle(eventParticipation));
		if(GameplayUtils.isActiveEvent(eventParticipation.getId())) {
			holder.eventItemTitle.setText(eventParticipation.getEvent().getTitle() + " " + activity.getString(R.string.active_event));
		}
	}

	@Override
	protected void fillChildViewInternal(final EventParticipation eventParticipation, ChildViewHolder holder) {
		holder.eventItemReactions.setVisibility(View.INVISIBLE);
		holder.eventItemJoin.setVisibility(View.INVISIBLE);
		holder.eventItemSelectActive.setVisibility(GameplayUtils.isActiveEvent(eventParticipation.getId()) ? View.INVISIBLE : View.VISIBLE);
		holder.eventItemSelectActive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelect.onSelect(eventParticipation);
			}
		});
	}
}
