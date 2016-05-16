package pl.looter.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.activities.MainActivity;
import pl.looter.appengine.userApi.model.User;
import pl.looter.utils.GameplayUtils;

public class AccountActivity extends AppCompatActivity {

	@Bind(R.id.account_name)
	TextView accountName;
	@Bind(R.id.account_level)
	TextView accountLevel;
	@Bind(R.id.account_experience_bar)
	ProgressBar accountExperienceBar;
	@Bind(R.id.account_experience_text)
	TextView accountExperienceText;
	@Bind(R.id.account_experience_container)
	FrameLayout accountExperienceContainer;
	@Bind(R.id.account_back)
	Button accountBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		ButterKnife.bind(this);

		User user = DataHolder.getInstance().getUser();
		if(user == null) {
			accountName.setText(getString(R.string.no_results));
			accountExperienceText.setVisibility(View.INVISIBLE);
			accountExperienceBar.setVisibility(View.INVISIBLE);
		} else {
			accountName.setText(user.getName());
			accountLevel.setText(getString(R.string.level) + " " + user.getLevel());

			int xpForThisLevel = GameplayUtils.getXpForLevel(user.getLevel());
			int xpForNextLevel = GameplayUtils.getXpForLevel(user.getLevel() + 1);

			accountExperienceText.setText("" + user.getXp() + " / " + xpForNextLevel);
			accountExperienceText.setTextColor(Color.BLACK);

			accountExperienceBar.setMax(xpForNextLevel - xpForThisLevel);
			accountExperienceBar.setProgress(user.getXp().intValue() - xpForThisLevel);
		}
	}

	@OnClick(R.id.account_back)
	public void onClick() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}
}
