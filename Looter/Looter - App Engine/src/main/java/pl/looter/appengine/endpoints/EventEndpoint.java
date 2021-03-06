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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import pl.looter.appengine.domain.Event;
import pl.looter.appengine.domain.User;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "eventApi",
        version = "v1",
        resource = "event",
        namespace = @ApiNamespace(
                ownerDomain = "domain.appengine.looter.pl",
                ownerName = "domain.appengine.looter.pl",
                packagePath = ""
        )
)
public class EventEndpoint {

    private static final Logger logger = Logger.getLogger(EventEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Event.class);
    }

    /**
     * Returns the {@link Event} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Event} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Event get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Event with ID: " + id);
        Event event = ofy().load().type(Event.class).id(id).now();
        if (event == null) {
            throw new NotFoundException("Could not find Event with ID: " + id);
        }
        return event;
    }

    /**
     * Inserts a new {@code Event}.
     */
    @ApiMethod(
            name = "insert",
            path = "event",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Event insert(Event event) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that event.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(event).now();
        logger.info("Created Event with ID: " + event.getId());

        return ofy().load().entity(event).now();
    }

    /**
     * Updates an existing {@code Event}.
     *
     * @param id    the ID of the entity to be updated
     * @param event the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "update",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Event update(@Named("id") Long id, Event event) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(event).now();
        logger.info("Updated Event: " + event);
        return ofy().load().entity(event).now();
    }

    /**
     * Deletes the specified {@code Event}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Event}
     */
    @ApiMethod(
            name = "remove",
            path = "event/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Event.class).id(id).now();
        logger.info("Deleted Event with ID: " + id);
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
            path = "event",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Event> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Event> query = ofy().load().type(Event.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            eventList.add(queryIterator.next());
        }
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "findUserEvents",
            path = "findUserEvents",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Event> findUserEvents(@Named("userId") Long userId, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        logger.info("Getting events of user "+userId);
        User user = ofy().load().type(User.class).id(userId).now();
        if(user == null) {
            return null;
        }

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Event> query = ofy().load().type(Event.class).filter("master", user).order("-date").limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            eventList.add(queryIterator.next());
        }
        logger.info("Results: "+eventList);
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "findUpcomingOpenEvents",
            path = "findUpcomingOpenEvents",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Event> findUpcomingOpenEvents(@Named("excludedUserId") Long excludedUserId, @Named("from") Date from, @Named("to") Date to, @Named("title") String title, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        logger.info("Getting upcoming events from "+from+" to "+to+" with title "+title);
	    limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
	    Query<Event> query = ofy().load().type(Event.class)
			    .filter("open", true)
			    .filter("date >", from.getTime())
			    .filter("date <", to.getTime());
        if(title != null && title.length() > 0) {
            query.filter("title", title);
        }
        query.limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Event> queryIterator = query.iterator();
        List<Event> eventList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            Event event = queryIterator.next();
            if(!excludedUserId.equals(event.getMaster().getId())) {
                eventList.add(event);
            }
        }
	    logger.info("Results: "+eventList);
        return CollectionResponse.<Event>builder().setItems(eventList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Event.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Event with ID: " + id);
        }
    }
}