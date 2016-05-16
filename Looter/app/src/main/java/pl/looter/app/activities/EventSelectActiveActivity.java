package pl.looter.app.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import pl.looter.app.interfaces.IOnSelect;
import pl.looter.app.list_adapters.EventSelectActiveListAdapter;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.domain.eventParticipationApi.model.EventStatus;
import pl.looter.utils.EndpointUtils;

public class EventSelectActiveActivity extends AppCompatActivity implements IOnSelect<EventParticipation> {

	private static final String TAG = EventSelectActiveActivity.class.getSimpleName();
	@Bind(R.id.event_select_active_info)
	TextView eventSelectActiveInfo;
	@Bind(R.id.event_select_active_list_view)
	ExpandableListView eventSelectActiveListView;
	@Bind(R.id.event_select_active_back)
	Button eventSelectActiveBack;

	private final EventParticipationApi eventParticipationApi = EndpointUtils.newEventParticipationApi();

	private List<EventParticipation> eventParticipations;
	private EventSelectActiveListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_select_active);
		ButterKnife.bind(this);

		eventSelectActiveInfo.setText(getString(R.string.my_events_fetching));

		new AsyncTask<Void, Void, List<EventParticipation>>() {

			@Override
			protected List<EventParticipation> doInBackground(Void... params) {
				try {
					return eventParticipationApi.listPendingEvents(DataHolder.getInstance().getUser().getId()).execute().getItems();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to load events in async task", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<EventParticipation> eventParticipations) {
				if (eventParticipations == null) {
					eventSelectActiveInfo.setText(getString(R.string.no_results));
				} else {
					eventSelectActiveInfo.setText(getString(R.string.empty));
					EventSelectActiveActivity.this.eventParticipations = eventParticipations;
					listAdapter = new EventSelectActiveListAdapter(eventParticipations, EventSelectActiveActivity.this, EventSelectActiveActivity.this);
					eventSelectActiveListView.setAdapter(listAdapter);
				}
			}
		}.execute();

	}

	@OnClick(R.id.event_select_active_back)
	public void onClick() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@Override
	public void onSelect(final EventParticipation eventParticipation) {
		final Dialog dialog = new Dialog(this);
		dialog.setTitle(getString(R.string.active_event_fetching));
		dialog.show();

		new AsyncTask<Void, Void, EventStatus>() {

			@Override
			protected EventStatus doInBackground(Void... params) {
				try {
					return eventParticipationApi.getEventStatus(eventParticipation.getId()).execute();
				} catch (IOException e) {
					Log.e(TAG, "failed to select active event");
					return null;
				}
			}

			@Override
			protected void onPostExecute(EventStatus eventStatus) {
				DataHolder.getInstance().setActiveEventStatus(eventStatus);
				listAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		}.execute();
	}
}
