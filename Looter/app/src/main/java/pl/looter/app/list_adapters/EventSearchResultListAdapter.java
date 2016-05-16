package pl.looter.app.list_adapters;

import android.app.Activity;
import android.view.View;

import java.util.List;

import pl.looter.R;
import pl.looter.app.interfaces.IOnSelect;
import pl.looter.appengine.domain.eventApi.model.Event;

/**
 * Created by Arjan on 23.04.2016.
 */
public class EventSearchResultListAdapter extends AbstractEventExpandableListAdapter {

	private final IOnSelect onSelect;

	public EventSearchResultListAdapter(List<Event> events, Activity activity, IOnSelect onSelect) {
		super(events, activity);
		this.onSelect = onSelect;
	}

	@Override
	protected void fillGroupViewInternal(Event event, GroupViewHolder holder) {
		holder.eventItemSubtitle.setText(activity.getString(R.string.event_master) + " " + event.getMaster().getName());
	}

	@Override
	protected void fillChildViewInternal(final Event event, ChildViewHolder holder) {
		holder.eventItemReactions.setVisibility(View.INVISIBLE);
		holder.eventItemJoin.setVisibility(View.INVISIBLE);
		holder.eventItemSelectActive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSelect.onSelect(event);
			}
		});
	}
}
