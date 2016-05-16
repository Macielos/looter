package pl.looter.app.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.looter.R;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.app.list_adapters.AbstractEventExpandableListAdapter;
import pl.looter.utils.TimeUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public class MyEventsListAdapter extends AbstractEventExpandableListAdapter {

	public MyEventsListAdapter(List<Event> events, Activity activity) {
		super(events, activity);
	}

	@Override
	protected void fillGroupViewInternal(Event event, AbstractEventExpandableListAdapter.GroupViewHolder holder) {
		holder.eventItemSubtitle.setText(TimeUtils.formatDate(event.getDate()));
	}

	@Override
	protected void fillChildViewInternal(Event event, AbstractEventExpandableListAdapter.ChildViewHolder holder) {
		holder.eventItemReactions.setVisibility(View.INVISIBLE);
		holder.eventItemJoin.setVisibility(View.INVISIBLE);
		holder.eventItemSelectActive.setVisibility(View.INVISIBLE);
	}

}
