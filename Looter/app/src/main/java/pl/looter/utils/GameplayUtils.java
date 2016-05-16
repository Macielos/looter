package pl.looter.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.looter.app.dataholder.DataHolder;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.domain.pointApi.model.JsonMap;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.appengine.domain.pointApi.model.PointTreeCollection;

/**
 * Created by Arjan on 30.04.2016.
 */
public abstract class GameplayUtils {

	private  GameplayUtils() {
	}

	public static int getXpForLevel(int level) {
		return level < 1 ? 0 : level * 1000;
	}

	public static boolean connectNode(PointTreeCollection collection, Point existingNode, Point nextNode, String existingMarkerId, String newMarkerId) {
		if(collection.getTrees() == null) {
			collection.setTrees(new JsonMap());
		}
		if(collection.getTreePoints() == null) {
			collection.setTreePoints(new JsonMap());
		}
		JsonMap trees = collection.getTrees();
		JsonMap treePoints = collection.getTreePoints();

		treePoints.put(newMarkerId, nextNode);

		if (existingMarkerId == null) {
			JsonMap newSingleNodeTree = new JsonMap();
			newSingleNodeTree.put(newMarkerId, null);
			trees.put(newMarkerId, newSingleNodeTree);
			return true;
		}

		for (Map.Entry<String, Object> treeEntry : trees.entrySet()) {
			JsonMap singleNodeConnections = (JsonMap) treeEntry.getValue();
			if (singleNodeConnections.containsKey(existingMarkerId)) {
				List<String> children = (List<String>) singleNodeConnections.get(existingMarkerId);
				if (children == null) {
					children = new ArrayList<>();
				}
				children.add(newMarkerId);
				singleNodeConnections.put(existingMarkerId, children);
				singleNodeConnections.put(newMarkerId, null);
				return true;
			}
		}
		return false;
	}

	public static boolean removeNode(PointTreeCollection collection, String idToRemove) {
		Point point = (Point) collection.getTreePoints().get(idToRemove);
		if(point == null) {
			return false;
		}

		for(Object o: collection.getTrees().values()) {
			JsonMap tree = (JsonMap) o;
			//czy punkt do usuniecie jest w tym drzewie
			if(tree.containsKey(idToRemove)) {
				//czy punkt do usuniecia jest lisciem
				if (tree.get(idToRemove) == null) {
					//usuwamy punkt z drzewa
					tree.remove(idToRemove);
					if (tree.isEmpty()) {
						//i jesli to był ostatni - usuwamy drzewo
						collection.getTrees().remove(idToRemove);
					}

					removeNodeFromTree(tree, idToRemove);

					//i usuwamy powiazanie z obiektem pkt
					collection.getTreePoints().remove(idToRemove);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	private static void removeNodeFromTree(JsonMap tree, String idToRemove) {
		Set<String> nullKeys = new HashSet<>();
		for(JsonMap.Entry entry: tree.entrySet()) {
			if(entry.getValue() != null) {
				List<String> children = (List<String>) entry.getValue();
				children.remove(idToRemove);
				if (children.isEmpty()) {
					nullKeys.add(entry.getKey().toString());
				}
			}
		}
		for(String s: nullKeys) {
			tree.put(s, null);
		}
	}

	public static String getEventStatusSubtitle(EventParticipation eventParticipation) {
		long startOfDay = TimeUtils.startOfDay().getTime();

		if(eventParticipation.getEventTime() > startOfDay) {
			long daysLeft = (eventParticipation.getEventTime() - startOfDay) / TimeUtils.DAY;
			return "in "+daysLeft+" days";
		}

		long now = TimeUtils.now();
		long startTime = eventParticipation.getEvent().getDate() + eventParticipation.getEvent().getStartTime();
		long endTime = eventParticipation.getEvent().getDate() + eventParticipation.getEvent().getEndTime();
		if(startTime > now) {
			return "Starts today at "+TimeUtils.formatTime(startTime);
		}
		if(endTime < now) {
			return "Already finished";
		}
		return "Takes place right now";
	}

	public static boolean isActiveEvent(Long eventParticipationId) {
		return DataHolder.getInstance().getActiveEventStatus() != null && DataHolder.getInstance().getActiveEventStatus().getEventParticipation().getId().equals(eventParticipationId);
	}
}