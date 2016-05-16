package pl.looter.appengine.startup;

import com.google.appengine.api.datastore.GeoPt;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import pl.looter.appengine.OfyService;
import pl.looter.appengine.domain.Event;
import pl.looter.appengine.domain.EventParticipation;
import pl.looter.appengine.domain.Message;
import pl.looter.appengine.domain.Point;
import pl.looter.appengine.domain.User;
import pl.looter.appengine.endpoints.EventEndpoint;
import pl.looter.appengine.endpoints.EventParticipationEndpoint;
import pl.looter.appengine.endpoints.PointEndpoint;
import pl.looter.appengine.endpoints.UserEndpoint;
import pl.looter.appengine.utils.TimeUtils;

/**
 * Created by Arjan on 23.04.2016.
 */
public class Initializer implements ServletContextListener {

    private final Logger log = Logger.getLogger(getClass().getCanonicalName());

    private final UserEndpoint userEndpoint = new UserEndpoint();
    private final EventEndpoint eventEndpoint = new EventEndpoint();
    private final EventParticipationEndpoint eventParticipationEndpoint = new EventParticipationEndpoint();
    private final PointEndpoint pointEndpoint = new PointEndpoint();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("init...");
        OfyService.init();
        clearDatastore();

        User user1 = userEndpoint.insert(new User("Aristaeus", "qwerty"));
        User user2 = userEndpoint.insert(new User("Orestes", "zxcvbn"));
	    User user3 = userEndpoint.insert(new User("Enesis", "asdfgh"));
	    User user4 = userEndpoint.insert(new User("Arvander", "uiop"));

        Event event1 = eventEndpoint.insert(new Event("napierdalanie nieumarłych", "Miecze w dłoń i do boju", TimeUtils.daysAgo(-1), TimeUtils.hour(8, 0), TimeUtils.hour(12, 0), user1, true));
        Event event2 = eventEndpoint.insert(new Event("napierdalanie inkwizytorow", "Bić skurwysynów", TimeUtils.daysAgo(-4), TimeUtils.hour(10, 30), TimeUtils.hour(18, 45), user2, true));
	    Event event3 = eventEndpoint.insert(new Event("napierdalanie elfów", "Dobry elf to martwy elf", TimeUtils.daysAgo(3), TimeUtils.hour(8, 0), TimeUtils.hour(12, 0), user1, true));
	    Event event4 = eventEndpoint.insert(new Event("napierdalanie orków", "Greeeeeeeen wave", TimeUtils.daysAgo(4), TimeUtils.hour(10, 30), TimeUtils.hour(18, 45), user2, true));

	    EventParticipation invitation = eventParticipationEndpoint.insert(new EventParticipation(user2, user1, event1));
        EventParticipation invitation2 = eventParticipationEndpoint.insert(new EventParticipation(user1, user2, event2));

        //PointEndpoint.PointTreeCollection treeCollection = exampleTreeCollection();
        //pointEndpoint.insertTreeCollection(treeCollection);

        /*
        Point point = new Point(new GeoPt(52.0f, 21.0f));
        point.setNextPoint(pointEndpoint.insert(new Point(new GeoPt(58.0f, 25.0f))));
        pointEndpoint.insert(point);
        */

        log.info("done");
    }

	private PointEndpoint.PointTreeCollection exampleTreeCollection() {
		PointEndpoint.PointTreeCollection treeCollection = new PointEndpoint.PointTreeCollection();

		Map<String, Point> treePoints = treeCollection.getTreePoints();
		treePoints.put("5050", new Point(50.0f, 50.0f));
		treePoints.put("1010", new Point(10.0f, 10.0f));
		treePoints.put("1515", new Point(15.0f, 15.0f));
		treePoints.put("0515", new Point(5.0f, 15.0f));
		treePoints.put("3510", new Point(35.0f, 10.0f));
		treePoints.put("3515", new Point(35.0f, 15.0f));
		treePoints.put("3020", new Point(30.0f, 20.0f));
		treePoints.put("3520", new Point(35.0f, 20.0f));
		treePoints.put("4020", new Point(40.0f, 20.0f));

		Map<String, List<String>> tree = new HashMap<>();
		tree.put("1010", Arrays.asList("1515", "0515"));

		Map<String, List<String>> tree2 = new HashMap<>();
		tree2.put("3510", Arrays.asList("3515"));
		tree2.put("3515", Arrays.asList("3020", "3520", "4020"));

		Map<String, List<String>> tree3 = new HashMap<>();
		tree3.put("5050", null);

		treeCollection.getTrees().put("1010", tree);
		treeCollection.getTrees().put("3510", tree2);
		treeCollection.getTrees().put("5050", tree3);

		return treeCollection;
	}

	@Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void clearDatastore(){
        for(Class c: new Class[]{User.class, Event.class, Point.class, EventParticipation.class, Message.class}){
            clearTable(c);
        }
    }

    private void clearTable(Class clazz){
        OfyService.ofy().delete().keys(OfyService.ofy().load().type(clazz).keys().list()).now();
    }
}
