package com.looter.endpoints;

import com.looter.EMF;
import com.looter.entities.Invitation;
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

@Api(name = "invitationendpoint", namespace = @ApiNamespace(ownerDomain = "looter.com", ownerName = "looter.com", packagePath = "entities") )
public class InvitationEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listInvitation")
	public CollectionResponse<Invitation> listInvitation(@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Invitation> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Invitation as Invitation");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Invitation>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Invitation obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Invitation> builder().setItems(execute).setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getInvitation")
	public Invitation getInvitation(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		Invitation invitation = null;
		try {
			invitation = mgr.find(Invitation.class, id);
		} finally {
			mgr.close();
		}
		return invitation;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param invitation the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertInvitation")
	public Invitation insertInvitation(Invitation invitation) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsInvitation(invitation)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(invitation);
		} finally {
			mgr.close();
		}
		return invitation;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param invitation the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateInvitation")
	public Invitation updateInvitation(Invitation invitation) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsInvitation(invitation)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(invitation);
		} finally {
			mgr.close();
		}
		return invitation;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeInvitation")
	public void removeInvitation(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			Invitation invitation = mgr.find(Invitation.class, id);
			mgr.remove(invitation);
		} finally {
			mgr.close();
		}
	}

	private boolean containsInvitation(Invitation invitation) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Invitation item = mgr.find(Invitation.class, invitation.getInviteeId());
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
