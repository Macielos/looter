package pl.looter.app.invitations;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.looter.R;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.utils.DateUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public class InvitationListAdapter extends BaseExpandableListAdapter {

	static class GroupViewHolder {
		@Bind(R.id.invitation_item_title)
		TextView invitationItemTitle;
		@Bind(R.id.invitation_item_inviter)
		TextView invitationItemInviter;

		GroupViewHolder(View view) {
			ButterKnife.bind(this, view);
		}

	}

	static class ChildViewHolder {
		@Bind(R.id.invitation_item_event_description)
		TextView invitationItemEventDescription;
		@Bind(R.id.invitation_item_event_start_time)
		TextView invitationItemEventStartTime;
		@Bind(R.id.invitation_item_event_end_time)
		TextView invitationItemEventEndTime;
		@Bind(R.id.invitation_item_accept)
		Button invitationAccept;
		@Bind(R.id.invitation_item_reject)
		Button invitationReject;

		ChildViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	private final List<EventParticipation> invitations;
	private final Activity activity;
	private final IOnInvitation onInvitation;

	public InvitationListAdapter(List<EventParticipation> invitations, Activity activity, IOnInvitation onInvitation) {
		this.invitations = invitations;
		this.activity = activity;
		this.onInvitation = onInvitation;
	}


	@Override
	public int getGroupCount() {
		return invitations.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return invitations.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return invitations.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return invitations.get(groupPosition).getId();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_invitation, null);
		GroupViewHolder holder = new GroupViewHolder(view);

		EventParticipation invitation = invitations.get(groupPosition);
		holder.invitationItemTitle.setText(invitation.getEvent().getTitle());
		holder.invitationItemInviter.setText(activity.getString(R.string.invitation_inviter) + " " + invitation.getMaster().getName());
		return view;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.item_invitation_details, null);
		final ChildViewHolder holder = new ChildViewHolder(view);

		final EventParticipation invitation = invitations.get(groupPosition);

		holder.invitationItemEventDescription.setText(invitation.getEvent().getDescription());
		holder.invitationItemEventStartTime.setText(DateUtils.format(invitation.getEvent().getStartTime().getValue()));
		holder.invitationItemEventEndTime.setText(DateUtils.format(invitation.getEvent().getEndTime().getValue()));

		holder.invitationAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.invitationAccept.setEnabled(false);
				onInvitation.onAccept(invitation);
				invitations.remove(invitation);
				notifyDataSetChanged();
			}
		});
		holder.invitationReject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.invitationReject.setEnabled(false);
				onInvitation.onReject(invitation);
				invitations.remove(invitation);
				notifyDataSetChanged();
			}
		});

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
