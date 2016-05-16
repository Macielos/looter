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
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.list_adapters.MyEventsListAdapter;
import pl.looter.appengine.domain.eventApi.EventApi;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.utils.EndpointUtils;

public class MyEventsActivity extends AppCompatActivity {

	@Bind(R.id.my_events_info)
	TextView myEventsInfo;
	@Bind(R.id.my_events_list_view)
	ExpandableListView myEventsListView;
	@Bind(R.id.my_events_back)
	Button myEventsBack;

	private List<Event> events = new ArrayList<>(0);

	private final EventApi eventApi = EndpointUtils.newEventApi();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_events);
		ButterKnife.bind(this);

		myEventsInfo.setText(getString(R.string.my_events_fetching));

		new AsyncTask<Void, Void, List<Event>>() {

			@Override
			protected List<Event> doInBackground(Void... params) {
				try {
					return eventApi.findUserEvents(DataHolder.getInstance().getUser().getId()).execute().getItems();
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), "Failed to load events in async task", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<Event> events) {
				if (events == null) {
					myEventsInfo.setText(getString(R.string.no_results));
				} else {
					myEventsInfo.setText(getString(R.string.empty));
					MyEventsActivity.this.events = events;
					myEventsListView.setAdapter(new MyEventsListAdapter(events, MyEventsActivity.this));
				}
			}
		}.execute();

	}

	@OnClick({R.id.my_events_back})
	public void onClick(View view) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

}
