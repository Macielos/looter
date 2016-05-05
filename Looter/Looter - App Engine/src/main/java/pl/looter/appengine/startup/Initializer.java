package pl.looter.appengine.startup;

import com.google.appengine.api.datastore.GeoPt;

import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import pl.looter.appengine.OfyService;
import pl.looter.appengine.domain.Event;
import pl.looter.appengine.domain.EventParticipation;
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
        Event event1 = eventEndpoint.insert(new Event("napierdalanie nieumarłych", "Miecze w dłoń i do boju", new Date(TimeUtils.daysAgo(-1)), new Date(TimeUtils.daysAgo(-3)), user1.getId()));
        Event event2 = eventEndpoint.insert(new Event("napierdalanie inkwizytorow", "Bić skurwysynów", new Date(TimeUtils.daysAgo(-1)), new Date(TimeUtils.daysAgo(-3)), user2.getId()));
        EventParticipation invitation = eventParticipationEndpoint.insert(new EventParticipation(user2, user1, event1));
        EventParticipation invitation2 = eventParticipationEndpoint.insert(new EventParticipation(user1, user2, event2));
        Point point = pointEndpoint.insert(new Point(new GeoPt(52.0f, 21.0f)));
        log.info("done");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void clearDatastore(){
        for(Class c: new Class[]{User.class, Event.class, Point.class, EventParticipation.class}){
            clearTable(c);
        }
    }

    private void clearTable(Class clazz){
        OfyService.ofy().delete().keys(OfyService.ofy().load().type(clazz).keys().list()).now();
    }
}
