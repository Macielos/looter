package pl.looter.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.list_adapters.InvitationListAdapter;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.utils.EndpointUtils;
import pl.looter.app.interfaces.IOnInvitation;

public class InvitationActivity extends AppCompatActivity implements IOnInvitation {

    @Bind(R.id.invitation_title)
    TextView invitationTitle;
    @Bind(R.id.invitation_info)
    TextView invitationInfo;
    @Bind(R.id.invitation_list_view)
    ExpandableListView invitationListView;
	@Bind(R.id.invitation_back)
	Button invitationBack;

    private EventParticipationApi eventParticipationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        ButterKnife.bind(this);

        eventParticipationApi = EndpointUtils.newEventParticipationApi();

        loadEventParticipations();
    }

    private void loadEventParticipations() {
	    invitationInfo.setText(getString(R.string.invitation_fetching));
	    if(DataHolder.getInstance().getInvitations()!=null) {
		    invitationListView.setAdapter(new InvitationListAdapter(DataHolder.getInstance().getInvitations(), InvitationActivity.this, InvitationActivity.this));
	    }

	    new AsyncTask<Void, Void, List<EventParticipation>>() {

            @Override
            protected List<EventParticipation> doInBackground(Void... params) {
                try {
	                //TODO
                    return eventParticipationApi.listUserInvitations(DataHolder.getInstance().getUser().getId()).execute().getItems();
                } catch (IOException e) {
                    Log.e(getClass().getSimpleName(), "Failed to load invitations in async task", e);
	                return null;
                }
            }

            @Override
            protected void onPostExecute(List<EventParticipation> invitations) {
                if(invitations == null) {
                    invitationInfo.setText(getString(R.string.no_results));
                } else {
	                invitationInfo.setText(getString(R.string.empty));
	                if(!invitations.equals(DataHolder.getInstance().getInvitations())) {
		                DataHolder.getInstance().setInvitations(invitations);
		                invitationListView.setAdapter(new InvitationListAdapter(invitations, InvitationActivity.this, InvitationActivity.this));
	                }
                }
            }
        }.execute();
    }

	@OnClick({R.id.invitation_back})
	public void onClick(View view) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@Override
	public void onAccept(final EventParticipation invitation) {
		invitationInfo.setText(getString(R.string.invitation_accepting));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					eventParticipationApi.acceptInvitation(invitation.getId()).execute();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to accept invitation in async task", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void nothing) {
				invitationInfo.setText(getString(R.string.invitation_accepted));
			}
		}.execute();
	}

	@Override
	public void onReject(final EventParticipation invitation) {
		invitationInfo.setText(getString(R.string.invitation_rejecting));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					eventParticipationApi.rejectInvitation(invitation.getId()).execute();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to reject invitation in async task", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void nothing) {
				invitationInfo.setText(getString(R.string.invitation_rejected));
			}
		}.execute();
	}
}
