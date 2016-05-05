package pl.looter.app.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.api.client.util.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.main.MainActivity;
import pl.looter.app.map.MapActivity;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.utils.Constants;

public class EventCreationActivity extends AppCompatActivity {

	@Bind(R.id.event_title)
	EditText eventTitle;
	@Bind(R.id.event_description)
	EditText eventDescription;
	@Bind(R.id.event_open)
	CheckBox eventOpen;
	@Bind(R.id.event_start_time)
	CalendarView eventStartTime;
	@Bind(R.id.event_to_map)
	Button eventToMap;
	@Bind(R.id.event_back)
	Button eventBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creation);
		ButterKnife.bind(this);
	}

	@OnClick({R.id.event_to_map, R.id.event_back})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.event_to_map:
				Event event = new Event().setTitle(eventTitle.getText().toString()).setDescription(eventDescription.getText().toString()).setOpen(eventOpen.isChecked())
						.setStartTime(new DateTime(eventStartTime.getDate())).setEndTime(new DateTime(eventStartTime.getDate())).setMasterId(DataHolder.getInstance().getUser().getId());
				DataHolder.getInstance().setLastEvent(event);

				Intent intent = new Intent(getApplicationContext(), MapActivity.class);
				intent.putExtra(Constants.MAP_MODE, MapActivity.MapMode.CREATE_EVENT.name());
				startActivity(intent);
				finish();
				break;
			case R.id.event_back:
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
				break;
		}
	}
}
