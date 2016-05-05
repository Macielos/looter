package pl.looter.appengine;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import pl.looter.appengine.domain.Event;
import pl.looter.appengine.domain.EventParticipation;
import pl.looter.appengine.domain.Point;
import pl.looter.appengine.domain.User;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    public static void init() {
        ObjectifyService.begin();
        ObjectifyService.register(User.class);
        ObjectifyService.register(Event.class);
        ObjectifyService.register(Point.class);
        ObjectifyService.register(EventParticipation.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
