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

import pl.looter.appengine.domain.Loot;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
		name = "lootApi",
		version = "v1",
		resource = "loot",
		namespace = @ApiNamespace(
				ownerDomain = "domain.appengine.looter.pl",
				ownerName = "domain.appengine.looter.pl",
				packagePath = ""
		)
)
public class LootEndpoint {

	private static final Logger logger = Logger.getLogger(LootEndpoint.class.getName());

	private static final int DEFAULT_LIST_LIMIT = 20;

	static {
		// Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
		ObjectifyService.register(Loot.class);
	}

	/**
	 * Returns the {@link Loot} with the corresponding ID.
	 *
	 * @param id the ID of the entity to be retrieved
	 * @return the entity with the corresponding ID
	 * @throws NotFoundException if there is no {@code Loot} with the provided ID.
	 */
	@ApiMethod(
			name = "get",
			path = "loot/{id}",
			httpMethod = ApiMethod.HttpMethod.GET)
	public Loot get(@Named("id") Long id) throws NotFoundException {
		logger.info("Getting Loot with ID: " + id);
		Loot loot = ofy().load().type(Loot.class).id(id).now();
		if (loot == null) {
			throw new NotFoundException("Could not find Loot with ID: " + id);
		}
		return loot;
	}

	/**
	 * Inserts a new {@code Loot}.
	 */
	@ApiMethod(
			name = "insert",
			path = "loot",
			httpMethod = ApiMethod.HttpMethod.POST)
	public Loot insert(Loot loot) {
		// Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
		// You should validate that loot.id has not been set. If the ID type is not supported by the
		// Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
		//
		// If your client provides the ID then you should probably use PUT instead.
		ofy().save().entity(loot).now();
		logger.info("Created Loot with ID: " + loot.getId());

		return ofy().load().entity(loot).now();
	}

	/**
	 * Updates an existing {@code Loot}.
	 *
	 * @param id   the ID of the entity to be updated
	 * @param loot the desired state of the entity
	 * @return the updated version of the entity
	 * @throws NotFoundException if the {@code id} does not correspond to an existing
	 *                           {@code Loot}
	 */
	@ApiMethod(
			name = "update",
			path = "loot/{id}",
			httpMethod = ApiMethod.HttpMethod.PUT)
	public Loot update(@Named("id") Long id, Loot loot) throws NotFoundException {
		// TODO: You should validate your ID parameter against your resource's ID here.
		checkExists(id);
		ofy().save().entity(loot).now();
		logger.info("Updated Loot: " + loot);
		return ofy().load().entity(loot).now();
	}

	/**
	 * Deletes the specified {@code Loot}.
	 *
	 * @param id the ID of the entity to delete
	 * @throws NotFoundException if the {@code id} does not correspond to an existing
	 *                           {@code Loot}
	 */
	@ApiMethod(
			name = "remove",
			path = "loot/{id}",
			httpMethod = ApiMethod.HttpMethod.DELETE)
	public void remove(@Named("id") Long id) throws NotFoundException {
		checkExists(id);
		ofy().delete().type(Loot.class).id(id).now();
		logger.info("Deleted Loot with ID: " + id);
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
			path = "loot",
			httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<Loot> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
		limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
		Query<Loot> query = ofy().load().type(Loot.class).limit(limit);
		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}
		QueryResultIterator<Loot> queryIterator = query.iterator();
		List<Loot> lootList = new ArrayList<Loot>(limit);
		while (queryIterator.hasNext()) {
			lootList.add(queryIterator.next());
		}
		return CollectionResponse.<Loot>builder().setItems(lootList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
	}

	private void checkExists(Long id) throws NotFoundException {
		try {
			ofy().load().type(Loot.class).id(id).safe();
		} catch (com.googlecode.objectify.NotFoundException e) {
			throw new NotFoundException("Could not find Loot with ID: " + id);
		}
	}
}