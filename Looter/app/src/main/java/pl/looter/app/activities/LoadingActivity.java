package pl.looter.app.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.appengine.userApi.UserApi;
import pl.looter.appengine.userApi.model.UserStatus;
import pl.looter.utils.EndpointUtils;

public class LoadingActivity extends AppCompatActivity {

	@Bind(R.id.loading_screen)
	ImageView loadingScreen;
	@Bind(R.id.loading_screen_info)
	TextView loadingScreenInfo;

	private final UserApi userApi = EndpointUtils.newUserApi();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		ButterKnife.bind(this);

		new AsyncTask<Void, Void, UserStatus>() {

			@Override
			protected UserStatus doInBackground(Void... params) {
				try {
					//TODO login
					return userApi.loginAndGetNotifications("Orestes").execute();
				} catch (IOException e) {
					Log.e(MainActivity.class.getSimpleName(), "failed to load user", e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(UserStatus userStatus) {
				if(userStatus != null) {
					DataHolder.getInstance().setUserStatus(userStatus);
				}
				goToMainMenu();
			}
		}.execute();
	}

	private void goToMainMenu() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}
}
