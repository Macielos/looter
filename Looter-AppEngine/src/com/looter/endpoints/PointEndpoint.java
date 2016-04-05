package com.looter.endpoints;

import com.looter.EMF;
import com.looter.entities.Point;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "pointendpoint", namespace = @ApiNamespace(ownerDomain = "looter.com", ownerName = "looter.com", packagePath = "entities") )
public class PointEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listPoint")
	public CollectionResponse<Point> listPoint(@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Point> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Point as Point");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Point>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Point obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Point> builder().setItems(execute).setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getPoint")
	public Point getPoint(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Point point = null;
		try {
			point = mgr.find(Point.class, id);
		} finally {
			mgr.close();
		}
		return point;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param point the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertPoint")
	public Point insertPoint(Point point) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsPoint(point)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(point);
		} finally {
			mgr.close();
		}
		return point;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param point the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updatePoint")
	public Point updatePoint(Point point) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsPoint(point)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(point);
		} finally {
			mgr.close();
		}
		return point;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removePoint")
	public void removePoint(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Point point = mgr.find(Point.class, id);
			mgr.remove(point);
		} finally {
			mgr.close();
		}
	}

	private boolean containsPoint(Point point) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Point item = mgr.find(Point.class, point.getId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
