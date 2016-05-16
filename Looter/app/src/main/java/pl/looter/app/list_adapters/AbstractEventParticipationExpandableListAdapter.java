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
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.utils.TimeUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public abstract class AbstractEventParticipationExpandableListAdapter extends AbstractExpandableListAdapter<EventParticipation> {


	static class GroupViewHolder {
		@Bind(R.id.event_item_title)
		TextView eventItemTitle;
		@Bind(R.id.event_item_subtitle)
		TextView eventItemSubtitle;

		GroupViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	static class ChildViewHolder {
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
		@Bind(R.id.event_item_accept_invitation)
		Button eventItemAcceptInvitation;
		@Bind(R.id.event_item_reject_invitation)
		Button eventItemRejectInvitation;
		@Bind(R.id.event_item_join)
		Button eventItemJoin;
		@Bind(R.id.event_item_select_active)
		Button eventItemSelectActive;

		ChildViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	public AbstractEventParticipationExpandableListAdapter(List<EventParticipation> eventParticipations, Activity activity) {
		super(eventParticipations, activity);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_event, null);
		GroupViewHolder holder = new GroupViewHolder(view);

		EventParticipation eventParticipation = items.get(groupPosition);
		holder.eventItemTitle.setText(eventParticipation.getEvent().getTitle());

		fillGroupViewInternal(eventParticipation, holder);

		return view;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_event_details, null);
		final ChildViewHolder holder = new ChildViewHolder(view);

		final EventParticipation eventParticipation = items.get(groupPosition);

		holder.eventItemDescription.setText(eventParticipation.getEvent().getDescription());
		holder.eventItemDate.setText(TimeUtils.formatDate(eventParticipation.getEvent().getDate()));
		holder.eventItemStartTime.setText(TimeUtils.formatTime(eventParticipation.getEvent().getStartTime()));
		holder.eventItemEndTime.setText(TimeUtils.formatTime(eventParticipation.getEvent().getEndTime()));

		fillChildViewInternal(eventParticipation, holder);

		return view;
	}

	protected abstract void fillGroupViewInternal(EventParticipation eventParticipation, GroupViewHolder holder);

	protected abstract void fillChildViewInternal(EventParticipation eventParticipation, ChildViewHolder holder);
}
