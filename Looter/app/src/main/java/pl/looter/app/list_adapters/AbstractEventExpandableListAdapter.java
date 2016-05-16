package pl.looter.app.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.looter.R;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.utils.TimeUtils;

/**
 * Created by Arjan on 09.05.2016.
 */
public abstract class AbstractEventExpandableListAdapter extends AbstractExpandableListAdapter<Event> {

	protected static class GroupViewHolder {
		@Bind(R.id.event_item_title)
		TextView eventItemTitle;
		@Bind(R.id.event_item_subtitle)
		TextView eventItemSubtitle;

		GroupViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	protected static class ChildViewHolder {
		@Bind(R.id.event_item_description)
		TextView eventItemDescription;
		@Bind(R.id.event_item_date)
		TextView eventItemDate;
		@Bind(R.id.event_item_start_time)
		TextView eventItemStartTime;
		@Bind(R.id.event_item_end_time)
		TextView eventItemEndTime;
		@Bind(R.id.event_item_reactions)
		LinearLayout eventItemReactions;
		@Bind(R.id.event_item_join)
		Button eventItemJoin;
		@Bind(R.id.event_item_select_active)
		Button eventItemSelectActive;

		ChildViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	public AbstractEventExpandableListAdapter(List<Event> items, Activity activity) {
		super(items, activity);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_event, null);
		GroupViewHolder holder = new GroupViewHolder(view);

		Event event = items.get(groupPosition);
		holder.eventItemTitle.setText(event.getTitle());

		fillGroupViewInternal(event, holder);

		return view;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_event_details, null);
		final ChildViewHolder holder = new ChildViewHolder(view);

		final Event event = items.get(groupPosition);

		holder.eventItemDescription.setText(event.getDescription());
		holder.eventItemDate.setText(TimeUtils.formatDate(event.getDate()));
		holder.eventItemStartTime.setText(TimeUtils.formatTime(event.getStartTime()));
		holder.eventItemEndTime.setText(TimeUtils.formatTime(event.getEndTime()));

		fillChildViewInternal(event, holder);

		return view;
	}

	protected abstract void fillGroupViewInternal(Event event, GroupViewHolder holder);

	protected abstract void fillChildViewInternal(Event event, ChildViewHolder holder);


}
