package pl.looter.app.interfaces;

import com.google.api.client.json.GenericJson;

import pl.looter.appengine.domain.eventApi.model.Event;

/**
 * Created by Arjan on 10.05.2016.
 */
public interface IOnSelect<T extends GenericJson> {
	void onSelect(T t);
}
