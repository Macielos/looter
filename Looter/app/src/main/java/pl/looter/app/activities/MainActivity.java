package pl.looter.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;

public class MainActivity extends AppCompatActivity {

	@Bind(R.id.title)
	TextView title;
	@Bind(R.id.open_map)
	Button openMap;
	@Bind(R.id.select_active_event)
	Button selectActiveEvent;
	@Bind(R.id.invitations)
	Button invitations;
	@Bind(R.id.notifications)
	Button notifications;
	@Bind(R.id.start_event)
	Button startEvent;
	@Bind(R.id.search_for_events)
	Button searchForEvents;
	@Bind(R.id.my_events)
	Button myEvents;
	@Bind(R.id.my_account)
	Button myAccount;
	@Bind(R.id.quit)
	Button quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(MainActivity.this);

		int notificationCount = DataHolder.getInstance().getNotificationCount();
		if (notificationCount > 0) {
			String notificationText = getText(R.string.notifications).toString() + "(" + notificationCount + ")";
			notifications.setText(notificationText);
		}
	}

	@OnClick({R.id.open_map, R.id.invitations, R.id.notifications, R.id.start_event, R.id.search_for_events, R.id.my_events, R.id.my_account, R.id.quit, R.id.select_active_event})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.open_map:
				newActivity(MapActivity.class);
				break;
			case R.id.select_active_event:
				newActivity(EventSelectActiveActivity.class);
				break;
			case R.id.invitations:
				newActivity(InvitationActivity.class);
				break;
			case R.id.notifications:
				break;
			case R.id.start_event:
				newActivity(EventCreationActivity.class);
				break;
			case R.id.search_for_events:
				newActivity(EventSearchActivity.class);
				break;
			case R.id.my_events:
				newActivity(MyEventsActivity.class);
				break;
			case R.id.my_account:
				newActivity(AccountActivity.class);
				break;
			case R.id.quit:
				finish();
				break;
		}
	}

	private void newActivity(Class activityClass) {
		startActivity(new Intent(getApplicationContext(), activityClass));
		finish();
	}

}
