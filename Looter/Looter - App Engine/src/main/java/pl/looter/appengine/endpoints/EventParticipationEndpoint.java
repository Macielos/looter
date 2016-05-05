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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import pl.looter.appengine.domain.Event;
import pl.looter.appengine.domain.EventParticipation;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
		name = "eventParticipationApi",
		version = "v1",
		resource = "eventParticipation",
		namespace = @ApiNamespace(
				ownerDomain = "domain.appengine.looter.pl",
				ownerName = "domain.appengine.looter.pl",
				packagePath = ""
		)
)
public class EventParticipationEndpoint {

	private static final Logger logger = Logger.getLogger(EventParticipationEndpoint.class.getName());

	private static final int DEFAULT_LIST_LIMIT = 20;

	static {
		// Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
		ObjectifyService.register(EventParticipation.class);
	}

	/**
	 * Returns the {@link EventParticipation} with the corresponding ID.
	 *
	 * @param id the ID of the entity to be retrieved
	 * @return the entity with the corresponding ID
	 * @throws NotFoundException if there is no {@code EventParticipation} with the provided ID.
	 */
	@ApiMethod(
			name = "get",
			path = "eventParticipation/{id}",
			httpMethod = ApiMethod.HttpMethod.GET)
	public EventParticipation get(@Named("id") Long id) throws NotFoundException {
		logger.info("Getting EventParticipation with ID: " + id);
		EventParticipation eventParticipation = ofy().load().type(EventParticipation.class).id(id).now();
		if (eventParticipation == null) {
			throw new NotFoundException("Could not find EventParticipation with ID: " + id);
		}
		return eventParticipation;
	}

	/**
	 * Inserts a new {@code EventParticipation}.
	 */
	@ApiMethod(
			name = "insert",
			path = "eventParticipation",
			httpMethod = ApiMethod.HttpMethod.POST)
	public EventParticipation insert(EventParticipation eventParticipation) {
		// Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
		// You should validate that eventParticipation.id has not been set. If the ID type is not supported by the
		// Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
		//
		// If your client provides the ID then you should probably use PUT instead.
		ofy().save().entity(eventParticipation).now();
		logger.info("Created EventParticipation.");

		return ofy().load().entity(eventParticipation).now();
	}

	/**
	 * Updates an existing {@code EventParticipation}.
	 *
	 * @param id                 the ID of the entity to be updated
	 * @param eventParticipation the desired state of the entity
	 * @return the updated version of the entity
	 * @throws NotFoundException if the {@code id} does not correspond to an existing
	 *                           {@code EventParticipation}
	 */
	@ApiMethod(
			name = "update",
			path = "eventParticipation/{id}",
			httpMethod = ApiMethod.HttpMethod.PUT)
	public EventParticipation update(@Named("id") Long id, EventParticipation eventParticipation) throws NotFoundException {
		// TODO: You should validate your ID parameter against your resource's ID here.
		checkExists(id);
		ofy().save().entity(eventParticipation).now();
		logger.info("Updated EventParticipation: " + eventParticipation);
		return ofy().load().entity(eventParticipation).now();
	}

	/**
	 * Deletes the specified {@code EventParticipation}.
	 *
	 * @param id the ID of the entity to delete
	 * @throws NotFoundException if the {@code id} does not correspond to an existing
	 *                           {@code EventParticipation}
	 */
	@ApiMethod(
			name = "remove",
			path = "eventParticipation/{id}",
			httpMethod = ApiMethod.HttpMethod.DELETE)
	public void remove(@Named("id") Long id) throws NotFoundException {
		checkExists(id);
		ofy().delete().type(EventParticipation.class).id(id).now();
		logger.info("Deleted EventParticipation with ID: " + id);
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
			path = "eventParticipation",
			httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<EventParticipation> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
		limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
		Query<EventParticipation> query = ofy().load().type(EventParticipation.class).limit(limit);
		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}
		QueryResultIterator<EventParticipation> queryIterator = query.iterator();
		List<EventParticipation> eventParticipationList = new ArrayList<EventParticipation>(limit);
		while (queryIterator.hasNext()) {
			eventParticipationList.add(queryIterator.next());
		}
		return CollectionResponse.<EventParticipation>builder().setItems(eventParticipationList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
	}



	@ApiMethod(
			name = "listUserInvitations",
			path = "listUserInvitations",
			httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<EventParticipation> listUserInvitations(@Named("userId") Long userId, @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
		limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
		Query<EventParticipation> query = ofy().load().type(EventParticipation.class).filter("inviteeId", userId).limit(limit);
		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}
		QueryResultIterator<EventParticipation> queryIterator = query.iterator();
		List<EventParticipation> invitationList = new ArrayList<>(limit);
		while (queryIterator.hasNext()) {
			invitationList.add(queryIterator.next());
		}
		return CollectionResponse.<EventParticipation>builder().setItems(invitationList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
	}

	@ApiMethod(
			name = "acceptInvitation",
			path = "acceptInvitation/{id}",
			httpMethod = ApiMethod.HttpMethod.PUT)
	public void acceptInvitation(@Named("id") Long id) throws NotFoundException {
		// TODO: You should validate your ID parameter against your resource's ID here.
		EventParticipation eventParticipation = get(id);
		if(eventParticipation == null) {
			logger.info("No EventParticipation with id "+id);
			return;
		}
		eventParticipation.setStatus(EventParticipation.Status.ACCEPTED);
		ofy().save().entity(eventParticipation).now();
		logger.info("Accepted EventParticipation: " + eventParticipation);

	}

	@ApiMethod(
			name = "rejectInvitation",
			path = "rejectInvitation/{id}",
			httpMethod = ApiMethod.HttpMethod.PUT)
	public void rejectInvitation(@Named("id") Long id) throws NotFoundException {
		EventParticipation eventParticipation = get(id);
		if(eventParticipation == null) {
			logger.info("No EventParticipation with id "+id);
			return;
		}
		eventParticipation.setStatus(EventParticipation.Status.REJECTED);
		ofy().save().entity(eventParticipation).now();
		logger.info("Rejected EventParticipation: " + eventParticipation);
	}

	private void checkExists(Long id) throws NotFoundException {
		try {
			ofy().load().type(EventParticipation.class).id(id).safe();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException("Could not find EventParticipation with ID: " + id);
		}
	}
}