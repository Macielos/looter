package pl.looter.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.userApi.UserApi;
import pl.looter.appengine.userApi.model.User;
import pl.looter.utils.Constants;
import pl.looter.utils.TimeUtils;
import pl.looter.utils.EndpointUtils;

public class EventCreationActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = EventCreationActivity.class.getSimpleName();

	@Bind(R.id.event_title)
	EditText eventTitle;
	@Bind(R.id.event_description)
	EditText eventDescription;
	@Bind(R.id.event_open)
	CheckBox eventOpen;
	@Bind(R.id.event_date)
	CalendarView eventDate;
	@Bind(R.id.event_start_time)
	EditText eventStartTime;
	@Bind(R.id.event_end_time)
	EditText eventEndTime;
	@Bind(R.id.invited_users_list)
	TextView invitedUsersList;
	@Bind(R.id.invited_users_container)
	LinearLayout invitedUsersContainer;
	@Bind(R.id.event_search_users)
	AutoCompleteTextView eventSearchUsers;
	@Bind(R.id.event_search_users_button)
	SearchView eventSearchUsersButton;
	@Bind(R.id.event_to_map)
	Button eventToMap;
	@Bind(R.id.event_back)
	Button eventBack;

	private final UserApi userApi = EndpointUtils.newUserApi();

	private Set<User> usersToInvite = new HashSet<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creation);
		ButterKnife.bind(this);

		invitedUsersContainer.setVisibility(usersToInvite.isEmpty() ? View.INVISIBLE : View.VISIBLE);
		eventSearchUsersButton.setOnSearchClickListener(this);
	}

	@OnClick({R.id.event_search_users_button, R.id.event_to_map, R.id.event_back})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.event_search_users_button:
				onSearchUsersClick();
				break;
			case R.id.event_to_map:
				onToMapClick();
				break;
			case R.id.event_back:
				onBackClick();
				break;
		}
	}

	private void onSearchUsersClick() {
		if (eventSearchUsers.getText() == null || eventSearchUsers.getText().toString().isEmpty()) {
			return;
		}
		final String names = eventSearchUsers.getText().toString();
		new AsyncTask<Void, Void, List<User>>() {

			@Override
			protected List<User> doInBackground(Void... params) {
				try {
					return userApi.findByNames(names).execute().getItems();
				} catch (IOException e) {
					Log.e(TAG, "failed to find users in async task", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(List<User> users) {
				User currentUser = DataHolder.getInstance().getUser();
				if (users != null && !users.isEmpty()) {
					for (User user : users) {
						if (!currentUser.getName().equals(user.getName())) {
							usersToInvite.add(user);
						}
					}

					if (!usersToInvite.isEmpty()) {
						invitedUsersContainer.setVisibility(View.VISIBLE);
						refreshUserList();
					}
				}
			}
		}.execute();
	}

	private void refreshUserList() {
		StringBuilder sb = new StringBuilder();
		for (User user : usersToInvite) {
			sb.append(user.getName()).append(" ");
		}
		invitedUsersList.setText(sb.toString());
	}

	private void onToMapClick() {
		//TODO eventStartTime.get
		Event event;
		try {
			event = new Event()
					.setTitle(eventTitle.getText().toString())
					.setDescription(eventDescription.getText().toString())
					.setOpen(eventOpen.isChecked())
					.setDate(TimeUtils.startOfDay(eventDate.getDate()).getTime())
					.setStartTime(TimeUtils.textToTime(eventStartTime.getText().toString()))
					.setEndTime(TimeUtils.textToTime(eventEndTime.getText().toString()))
					.setMaster(EndpointUtils.toEAUser(DataHolder.getInstance().getUser()));

			DataHolder.getInstance().setLastEvent(event);
			DataHolder.getInstance().setLastEventInvitedUsers(usersToInvite);

			Intent intent = new Intent(getApplicationContext(), MapActivity.class);
			intent.putExtra(Constants.MAP_MODE, MapActivity.MapMode.CREATE_EVENT.name());
			startActivity(intent);
			finish();
		} catch (ParseException e) {
			Log.e(TAG, "incorrect date", e);
		}
	}

	private void onBackClick() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

}
