package pl.looter.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.utils.Constants;

/**
 * Created by Arjan on 15.05.2016.
 */
public class PointReachedDialog extends Dialog {

	@Bind(R.id.point_title)
	TextView pointTitle;
	@Bind(R.id.point_message)
	TextView pointMessage;
	@Bind(R.id.point_xp)
	TextView pointXp;
	@Bind(R.id.point_loot_label)
	TextView pointLootLabel;
	@Bind(R.id.point_loot_image)
	ImageView pointLootImage;
	@Bind(R.id.point_ok)
	Button pointOk;

	private final DialogDisplayThread dialogDisplayThread;

	public PointReachedDialog(Context context, Point point, DialogDisplayThread dialogDisplayThread) {
		super(context);
		this.dialogDisplayThread = dialogDisplayThread;

		setContentView(R.layout.dialog_point_reached);

		pointTitle.setText(point.getMessage().getTitle());
		pointMessage.setText(point.getMessage().getMessage());
		pointXp.setText(context.getString(R.string.map_xp_gained, Constants.BASE_XP));
		pointLootLabel.setText(context.getString(R.string.map_loot_found, point.getLoot().getName()));
		Bitmap imageBitmap = DataHolder.getInstance().getOrCreateBitmap(point.getLoot(), context);
		if(imageBitmap != null) {
			pointLootImage.setImageBitmap(imageBitmap);
		}
	}

	@OnClick(R.id.point_ok)
	public void onClick() {
		dismiss();
		dialogDisplayThread.notifyAll();
	}
}
