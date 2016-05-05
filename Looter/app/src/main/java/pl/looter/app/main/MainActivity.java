package pl.looter.app.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.account.AccountActivity;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.event.EventCreationActivity;
import pl.looter.app.invitations.InvitationActivity;
import pl.looter.app.map.MapActivity;
import pl.looter.appengine.userApi.UserApi;
import pl.looter.appengine.userApi.model.User;
import pl.looter.utils.EndpointUtils;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.open_map)
    Button openMap;
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

    private final UserApi userApi = EndpointUtils.newUserApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
	    new AsyncTask<Void, Void, User>() {

		    @Override
		    protected User doInBackground(Void... params) {
			    try {
				    //TODO login
				    return userApi.login("Orestes").execute();
			    } catch (IOException e) {
				    Log.e(MainActivity.class.getSimpleName(), "failed to load user", e);
				    return null;
			    }
		    }

		    @Override
		    protected void onPostExecute(User user) {
			    if(user != null) {
				    DataHolder.getInstance().setUser(user);
			    }
		    }
	    }.execute();
    }

    @OnClick({R.id.open_map, R.id.invitations, R.id.notifications, R.id.start_event, R.id.search_for_events, R.id.my_events, R.id.my_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_map:
                newActivity(MapActivity.class);
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
                break;
            case R.id.my_events:
                break;
            case R.id.my_account:
                newActivity(AccountActivity.class);
                break;
        }
    }

    private void newActivity(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
        finish();
    }
}
