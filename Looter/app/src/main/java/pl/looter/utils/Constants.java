package pl.looter.utils;

/**
 * Created by Arjan on 05.05.2016.
 */
public interface Constants {

	String MAP_MODE = "MapMode";

	String INVITED = "INVITED";
	String REQUESTED = "REQUESTED";

	String FROM = "from";
	String TO = "to";
	String TITLE = "title";

	//meters_per_pixel = 156543.03392 * Math.cos(latLng.lat() * Math.PI / 180) / Math.pow(2, zoom)
	float INITIAL_ZOOM = 16.0f;
	float MIN_QUEST_DISTANCE = 0.0001f;

	int QUEUE_SIZE = 10;
	int BASE_XP = 100;

}
