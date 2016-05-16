package pl.looter.app.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.looter.R;
import pl.looter.app.interfaces.IOnInvitation;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.utils.TimeUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public class InvitationListAdapter extends AbstractEventParticipationExpandableListAdapter {

	private final IOnInvitation onInvitation;

	public InvitationListAdapter(List<EventParticipation> invitations, Activity activity, IOnInvitation onInvitation) {
		super(invitations, activity);
		this.onInvitation = onInvitation;
	}

	@Override
	protected void fillGroupViewInternal(EventParticipation eventParticipation, GroupViewHolder holder) {
		holder.eventItemSubtitle.setText(activity.getString(R.string.invitation_inviter) + " " + eventParticipation.getMaster().getName());
	}

	@Override
	protected void fillChildViewInternal(final EventParticipation eventParticipation, final ChildViewHolder holder) {
		holder.eventItemJoin.setVisibility(View.INVISIBLE);
		holder.eventItemSelectActive.setVisibility(View.INVISIBLE);
		holder.eventItemAcceptInvitation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.eventItemAcceptInvitation.setEnabled(false);
				onInvitation.onAccept(eventParticipation);
				items.remove(eventParticipation);
				notifyDataSetChanged();
			}
		});
		holder.eventItemRejectInvitation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.eventItemRejectInvitation.setEnabled(false);
				onInvitation.onReject(eventParticipation);
				items.remove(eventParticipation);
				notifyDataSetChanged();
			}
		});
	}
}
