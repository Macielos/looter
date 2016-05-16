package pl.looter.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.utils.Constants;
import pl.looter.utils.TimeUtils;

public class EventSearchActivity extends AppCompatActivity {

	@Bind(R.id.event_search_from)
	CalendarView eventSearchFrom;
	@Bind(R.id.event_search_to)
	CalendarView eventSearchTo;
	@Bind(R.id.event_search_by_title)
	AutoCompleteTextView eventSearchByTitle;
	@Bind(R.id.event_search_info)
	TextView eventSearchInfo;
	@Bind(R.id.event_search)
	Button eventSearch;
	@Bind(R.id.event_search_back)
	Button eventSearchBack;

	private Date searchFrom = TimeUtils.startOfDay();
	private Date searchTo = TimeUtils.startOfDay();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_search);
		ButterKnife.bind(this);

		eventSearchFrom.setDate(TimeUtils.startOfDay().getTime());
		eventSearchTo.setDate(TimeUtils.startOfDay().getTime());
		eventSearchFrom.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				setSearchFrom(new GregorianCalendar(year, month, dayOfMonth).getTime());
			}
		});
		eventSearchTo.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				setSearchTo(new GregorianCalendar(year, month, dayOfMonth).getTime());
			}
		});
	}

	@OnClick({R.id.event_search})
	public void onSearchClick(View view) {
		final Date searchFrom = TimeUtils.startOfDay(this.searchFrom.getTime());
		final Date searchTo = TimeUtils.startOfDay(this.searchTo.getTime());
		final String searchTitle = eventSearchByTitle.getText().toString();

		if(searchFrom.getTime() > searchTo.getTime()) {
			eventSearchInfo.setText(getString(R.string.wrong_dates));
			return;
		}

		Intent intent = new Intent(getApplicationContext(), EventSearchResultActivity.class);
		intent.putExtra(Constants.FROM, searchFrom.getTime());
		intent.putExtra(Constants.TO, searchTo.getTime());
		intent.putExtra(Constants.TITLE, searchTitle);

		startActivity(intent);
		finish();
	}

	@OnClick({R.id.event_search_back})
	public void onBackClick(View view) {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}


	private void setSearchTo(Date searchTo) {
		this.searchTo = searchTo;
	}

	private void setSearchFrom(Date searchFrom) {
		this.searchFrom = searchFrom;
	}

}
