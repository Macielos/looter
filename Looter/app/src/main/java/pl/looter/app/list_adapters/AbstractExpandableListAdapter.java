package pl.looter.app.list_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.client.json.GenericJson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.looter.R;
import pl.looter.appengine.domain.eventApi.model.Event;

/**
 * Created by Arjan on 09.05.2016.
 */
public abstract class AbstractExpandableListAdapter<T extends GenericJson> extends BaseExpandableListAdapter {

	protected final List<T> items;
	protected final Activity activity;

	public AbstractExpandableListAdapter(List<T> items, Activity activity) {
		this.items = items;
		this.activity = activity;
	}

	@Override
	public int getGroupCount() {
		return items.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return items.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return items.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
