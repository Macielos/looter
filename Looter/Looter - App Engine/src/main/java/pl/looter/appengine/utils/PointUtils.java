package pl.looter.appengine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.looter.appengine.domain.Point;
import pl.looter.appengine.endpoints.PointEndpoint;

/**
 * Created by Arjan on 16.05.2016.
 */
public abstract class PointUtils {

	public static PointEndpoint.PointTreeCollection pointTreeCollection(Collection<Point> points) {
		PointEndpoint.PointTreeCollection pointTreeCollection = new PointEndpoint.PointTreeCollection();

		Map<Long, Point> pointsById = new HashMap<>(points.size());
		for(Point point: points) {
			pointsById.put(point.getId(), point);
			pointTreeCollection.getTreePoints().put(point.getId().toString(), point);
			if(point.isRoot()) {
				pointTreeCollection.getTrees().put(point.getId().toString(), new HashMap<String, List<String>>());
			}
		}

		for(Map.Entry<String, Map<String, List<String>>> entry: pointTreeCollection.getTrees().entrySet()) {
			buildTree(entry.getKey(), entry.getValue(), pointTreeCollection.getTreePoints());
		}

		return pointTreeCollection;
	}

	private static void buildTree(String pointId, Map<String, List<String>> tree, Map<String, Point> treePoints) {
		Point point = treePoints.get(pointId);
		List<String> children = new ArrayList<>();
		if(point.getNextPointIds() != null) {
			for(Long childId: point.getNextPointIds()) {
				children.add(childId.toString());
			}
		}
		if(children.isEmpty()) {
			tree.put(pointId, null);
		} else {
			tree.put(pointId, children);
			for(String childId: children) {
				buildTree(childId, tree, treePoints);
			}
		}
	}
}
