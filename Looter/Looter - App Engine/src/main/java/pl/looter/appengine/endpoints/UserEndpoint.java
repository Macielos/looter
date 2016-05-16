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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import lombok.Data;
import pl.looter.appengine.domain.EventParticipation;
import pl.looter.appengine.domain.User;
import pl.looter.appengine.utils.TimeUtils;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "userApi",
        version = "v1",
        resource = "user",
        namespace = @ApiNamespace(
                ownerDomain = "appengine.looter.pl",
                ownerName = "appengine.looter.pl",
                packagePath = ""
        )
)
public class UserEndpoint {

    private static final Logger logger = Logger.getLogger(UserEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(User.class);
    }

    private final EventParticipationEndpoint eventParticipationEndpoint = new EventParticipationEndpoint();

    /**
     * Returns the {@link User} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code User} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public User get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting User with ID: " + id);
        User user = ofy().load().type(User.class).id(id).now();
        if (user == null) {
            throw new NotFoundException("Could not find User with ID: " + id);
        }
        return user;
    }

    @ApiMethod(
            name = "loginAndGetNotifications",
            path = "loginAndGetNotifications/{name}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserStatus loginAndGetNotifications(@Named("name") String name) throws NotFoundException {
        logger.info("Login User: " + name);
        //TODO authentication via OAuth or sth else
        List<User> users = ofy().load().type(User.class).filter("name", name).list();
	    if(users == null || users.isEmpty()) {
		    return null;
	    }
	    User user = users.get(0);

	    UserStatus userStatus = new UserStatus();
	    userStatus.user = user;

        userStatus.pendingEvents = new ArrayList<>(eventParticipationEndpoint.listPendingEvents(user.getId(), null, null).getItems());

        Query<EventParticipation> requestsReceivedQuery = ofy().load().type(EventParticipation.class).filter("status", EventParticipation.Status.REQUESTED.name()).filter("master", user);
        Query<EventParticipation> requestsAcceptedQuery = ofy().load().type(EventParticipation.class).filter("status", EventParticipation.Status.ACCEPTED.name()).filter("master", user);
        Query<EventParticipation> invitationsReceivedQuery = ofy().load().type(EventParticipation.class).filter("status", EventParticipation.Status.INVITED.name()).filter("participant", user);
        Query<EventParticipation> invitationsAcceptedQuery = ofy().load().type(EventParticipation.class).filter("status", EventParticipation.Status.ACCEPTED.name()).filter("participant", user);

        if(user.getLastLogin() != null) {
            requestsReceivedQuery.filter("sendTime >", user.getLastLogin().getTime());
            requestsAcceptedQuery.filter("acceptTime >", user.getLastLogin().getTime());
            invitationsReceivedQuery.filter("sendTime >", user.getLastLogin().getTime());
            invitationsAcceptedQuery.filter("acceptTime >", user.getLastLogin().getTime());
        }
        userStatus.eventRequestsReceived = requestsReceivedQuery.list();
        userStatus.eventRequestsAccepted = requestsAcceptedQuery.list();
        userStatus.invitationsAccepted = invitationsAcceptedQuery.list();
        userStatus.invitationsReceived = invitationsReceivedQuery.list();

        user.setLastLogin(new Date());
	    ofy().save().entity(user).now();

        logger.info("Returning UserStatus: " + userStatus);
        return userStatus;
    }

    @ApiMethod(
            name = "findByNames",
            path = "findByNames/{names}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<User> findByNames(@Named("names") String names) throws NotFoundException {
        logger.info("FindByNames: " + names);
        List<User> users = ofy().load().type(User.class).filter("name IN", names.split(" ")).list();
        return CollectionResponse.<User>builder().setItems(users).build();
    }

    /**
     * Inserts a new {@code User}.
     */
    @ApiMethod(
            name = "insert",
            path = "user",
            httpMethod = ApiMethod.HttpMethod.POST)
    public User insert(User user) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that user.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(user).now();
        logger.info("Created User.");

        return ofy().load().entity(user).now();
    }

    /**
     * Updates an existing {@code User}.
     *
     * @param id   the ID of the entity to be updated
     * @param user the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code User}
     */
    @ApiMethod(
            name = "update",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public User update(@Named("id") Long id, User user) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(user).now();
        logger.info("Updated User: " + user);
        return ofy().load().entity(user).now();
    }

    /**
     * Deletes the specified {@code User}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code User}
     */
    @ApiMethod(
            name = "remove",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(User.class).id(id).now();
        logger.info("Deleted User with ID: " + id);
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
            path = "user",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<User> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<User> query = ofy().load().type(User.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<User> queryIterator = query.iterator();
        List<User> userList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            userList.add(queryIterator.next());
        }
        return CollectionResponse.<User>builder().setItems(userList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(User.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find User with ID: " + id);
        }
    }

    @Data
    static class UserStatus implements Serializable {
        private User user;
        private List<EventParticipation> pendingEvents;

        private List<EventParticipation> eventRequestsReceived;
	    private List<EventParticipation> invitationsAccepted;

        private List<EventParticipation> eventRequestsAccepted;
        private List<EventParticipation> invitationsReceived;

    }
}