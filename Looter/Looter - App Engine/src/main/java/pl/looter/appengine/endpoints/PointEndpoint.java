package pl.looter.appengine.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import pl.looter.appengine.domain.Point;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "pointApi",
        version = "v1",
        resource = "point",
        namespace = @ApiNamespace(
                ownerDomain = "domain.appengine.looter.pl",
                ownerName = "domain.appengine.looter.pl",
                packagePath = ""
        )
)
public class PointEndpoint {

    private static final Logger logger = Logger.getLogger(PointEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Point.class);
    }

    /**
     * Returns the {@link Point} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Point} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "point/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Point get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Point with ID: " + id);
        Point point = ofy().load().type(Point.class).id(id).now();
        if (point == null) {
            throw new NotFoundException("Could not find Point with ID: " + id);
        }
        return point;
    }

    /**
     * Inserts a new {@code Point}.
     */
    @ApiMethod(
            name = "insertTreeCollection",
            path = "insertTreeCollection",
            httpMethod = ApiMethod.HttpMethod.POST)
    public PointTreeCollection insertTreeCollection(PointTreeCollection pointTrees) {
	    logger.info("inserting trees " + pointTrees);
	    for(Map.Entry<String, Map<String, List<String>>> treeEntry: pointTrees.trees.entrySet()) {
		    insertTree(treeEntry.getKey(), treeEntry.getValue(), pointTrees.treePoints);
	    }
	    return pointTrees;
    }

	private void insertTree(String node, Map<String, List<String>> tree, Map<String, Point> treePoints) {
		Point nodePoint = treePoints.get(node);
		if(tree != null) {
			List<String> connections = tree.get(node);
			if (connections != null) {
				for (String nextNode : connections) {
					insertTree(nextNode, tree, treePoints);
					logger.info("connecting " + node + " to " + nextNode);

					nodePoint.addNextPoint(treePoints.get(nextNode).getId());
				}
			}
		}
		logger.info("inserting "+node);
		nodePoint = insert(nodePoint);
		treePoints.put(node, nodePoint);
	}


/*
	for (int i = connections.size() - 1; i >= 0; --i) {
		Point point = pointList.get(i);
		if (i < pointList.size() - 1) {
			point.setNextPoint(pointList.get(i + 1));
		}
		point = insert(point);
		pointList.set(i, point);
	}
}
}
		}*/

    @ApiMethod(
		    name = "insert",
		    path = "point",
		    httpMethod = ApiMethod.HttpMethod.POST)
    public Point insert(Point point) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that point.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.;
        ofy().save().entity(point).now();
        logger.info("Created Point: " + point);

        return ofy().load().entity(point).now();
    }


    /**
     * Updates an existing {@code Point}.
     *
     * @param id    the ID of the entity to be updated
     * @param point the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Point}
     */
    @ApiMethod(
            name = "update",
            path = "point/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Point update(@Named("id") Long id, Point point) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(point).now();
        logger.info("Updated Point: " + point);
        return ofy().load().entity(point).now();
    }

    /**
     * Deletes the specified {@code Point}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Point}
     */
    @ApiMethod(
            name = "remove",
            path = "point/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Point.class).id(id).now();
        logger.info("Deleted Point with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "point",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Point> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Point> query = ofy().load().type(Point.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Point> queryIterator = query.iterator();
        List<Point> pointList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            pointList.add(queryIterator.next());
        }
        return CollectionResponse.<Point>builder().setItems(pointList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

	private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Point.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Point with ID: " + id);
        }
    }

	public static class PointTreeCollection implements Serializable {
		private Map<String, Point> treePoints = new HashMap<>();
		private Map<String, Map<String, List<String>>> trees = new HashMap<>();

		public Map<String, Point> getTreePoints() {
			return treePoints;
		}

		public void setTreePoints(Map<String, Point> treePoints) {
			this.treePoints = treePoints;
		}

		public Map<String, Map<String, List<String>>> getTrees() {
			return trees;
		}

		public void setTrees(Map<String, Map<String, List<String>>> trees) {
			this.trees = trees;
		}

		@Override
		public String toString() {
			return "PointTreeCollection{" +
					"treePoints=" + treePoints +
					", trees=" + trees +
					'}';
		}
	}
}