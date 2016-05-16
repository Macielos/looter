package pl.looter.app.dataholder;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pl.looter.appengine.domain.eventParticipationApi.model.EventStatus;
import pl.looter.appengine.domain.pointApi.model.GeoPt;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.utils.Constants;

/**
 * Created by Arjan on 15.05.2016.
 */
public class EventStatusHolder {

	private final EventStatus eventStatus;

	private Set<String> visiblePoints;

	private final Map<Long, Double> eventPointsDistances;

	private LatLng currentUserLocation;

	public EventStatusHolder(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
		for (String rootId : eventStatus.getPointTreeCollection().getTrees().keySet()) {
			visiblePoints.add(rootId);
		}
		for(Long visitedPointId: eventStatus.getEventParticipation().getVisitedPoints()) {
			Point point = (Point) eventStatus.getPointTreeCollection().getTreePoints().get(visitedPointId.toString());
			markPointAsVisited(point);
		}

		this.eventPointsDistances = new HashMap<>();
	}

	public Set<Point> processNewUserLocation(LatLng currentUserLocation) {
		this.currentUserLocation = currentUserLocation;
		Set<Point> pointsInRange = null;
		for(String pointId: visiblePoints) {
			Point point = (Point) eventStatus.getPointTreeCollection().getTreePoints().get(pointId);
			double distance = distance(currentUserLocation, point.getGeoPoint());
			if(distance <= Constants.MIN_QUEST_DISTANCE) {
				if(pointsInRange == null) {
					pointsInRange = new HashSet<>();
				}
				pointsInRange.add(point);
				//TODO display msg, give loot etc.
				markPointAsVisited(point);
			}
			eventPointsDistances.put(point.getId(), distance);
		}

		return pointsInRange;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	private double distance(LatLng point1, GeoPt point2) {
		return Math.sqrt((point1.latitude - point2.getLatitude()) * (point1.latitude - point2.getLatitude()) + (point1.longitude - point2.getLongitude()) * (point1.longitude - point2.getLongitude()));
	}

	private void markPointAsVisited(Point visitedPoint) {
		eventStatus.getEventParticipation().getVisitedPoints().add(visitedPoint.getId());
		visiblePoints.remove(visitedPoint.getId().toString());
		if(visitedPoint.getNextPointIds() != null) {
			for (Long childId : visitedPoint.getNextPointIds()) {
				visiblePoints.add(childId.toString());
			}
		}
	}
}
