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

import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.list_adapters.EventSearchResultListAdapter;
import pl.looter.appengine.domain.eventApi.EventApi;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.domain.eventParticipationApi.model.User;
import pl.looter.utils.Constants;
import pl.looter.app.interfaces.IOnSelect;
import pl.looter.utils.TimeUtils;
import pl.looter.utils.EndpointUtils;

public class EventSearchResultActivity extends AppCompatActivity implements IOnSelect<Event> {

	@Bind(R.id.event_search_result_info)
	TextView eventSearchResultInfo;
	@Bind(R.id.event_search_result_list_view)
	ExpandableListView eventSearchResultListView;
	@Bind(R.id.event_search_result_new_query)
	Button eventSearchResultNewQuery;
	@Bind(R.id.event_search_result_back)
	Button eventSearchResultBack;

	private List<Event> events = new ArrayList<>(0);

	private final EventApi eventApi = EndpointUtils.newEventApi();
	private final EventParticipationApi eventParticipationApi = EndpointUtils.newEventParticipationApi();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_search_results);
		ButterKnife.bind(this);

		eventSearchResultInfo.setText(getString(R.string.event_searching));

		final Date searchFrom = new Date(getIntent().getLongExtra(Constants.FROM, 0));
		final Date searchTo = new Date(getIntent().getLongExtra(Constants.TO, 0));
		final String searchTitle = getIntent().getStringExtra(Constants.TITLE);

		new AsyncTask<Void, Void, List<Event>>() {

			@Override
			protected List<Event> doInBackground(Void... params) {
				try {
					return eventApi.findUpcomingOpenEvents(DataHolder.getInstance().getUser().getId(), new DateTime(searchFrom), searchTitle, new DateTime(searchTo)).execute().getItems();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to load events in async task", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<Event> events) {
				if (events == null) {
					eventSearchResultInfo.setText(getString(R.string.no_results));
				} else {
					eventSearchResultInfo.setText(getString(R.string.empty));
					EventSearchResultActivity.this.events = events;
					eventSearchResultListView.setAdapter(new EventSearchResultListAdapter(events, EventSearchResultActivity.this, EventSearchResultActivity.this));
				}
			}
		}.execute();

	}

	@OnClick({R.id.event_search_result_back})
	public void onBackClick(View view) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@OnClick({R.id.event_search_result_new_query})
	public void onNewQueryClick(View view) {
		startActivity(new Intent(getApplicationContext(), EventSearchActivity.class));
		finish();
	}

	@Override
	public void onSelect(final Event event) {
		eventSearchResultInfo.setText(getString(R.string.event_sending_request));

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					eventParticipationApi.insert(new EventParticipation()
							.setEvent(new pl.looter.appengine.domain.eventParticipationApi.model.Event().setId(event.getId()))
							.setMaster(new User().setId(event.getMaster().getId()))
							.setParticipant(new User().setId(DataHolder.getInstance().getUser().getId()))
							.setEventTime(event.getDate())
							.setSendTime(TimeUtils.now())
							.setStatus(Constants.REQUESTED))
							.execute();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to accept invitation in async task", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void nothing) {
				eventSearchResultInfo.setText(getString(R.string.event_request_sent));
			}
		}.execute();
	}
}
