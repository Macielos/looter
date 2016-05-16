package pl.looter.app.dialogs;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.BlockingQueue;

import pl.looter.app.activities.MapActivity;
import pl.looter.appengine.domain.pointApi.model.Point;

/**
 * Created by Arjan on 16.05.2016.
 */
public class DialogDisplayThread extends Thread {

	private static final String TAG = DialogDisplayThread.class.getSimpleName();

	private static final long SLEEP_TIME = 5000L;

	private final BlockingQueue<Point> queue;
	private final Context context;

	private volatile boolean running;

	public DialogDisplayThread(Context context, BlockingQueue<Point> queue) {
		this.context = context;
		this.queue = queue;
		running = true;
	}

	@Override
	public void run() {
		while(running) {
			Point point = queue.poll();
			Log.i(TAG, "Retrieving point "+point);
			if(point != null) {
				new PointReachedDialog(context, point, this).show();
			}
			try {
				wait();
			} catch (InterruptedException e) {
				Log.e(TAG, "wait interrupted", e);
			}
		}
	}
}
