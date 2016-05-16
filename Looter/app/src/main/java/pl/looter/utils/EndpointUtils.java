package pl.looter.utils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

import pl.looter.appengine.domain.eventApi.EventApi;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.appengine.domain.eventParticipationApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.model.User;
import pl.looter.appengine.domain.pointApi.PointApi;
import pl.looter.appengine.userApi.UserApi;

/**
 * Created by Arjan on 23.04.2016.
 */
public abstract class EndpointUtils {

    private static final boolean LOCAL_ANDROID_RUN = true;

    private static final String LOCAL_APP_ENGINE_SERVER_URL = "http://localhost:8080/";

    private static final String LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID = "http://10.0.2.2:8080";

    private EndpointUtils(){

    }

    public static UserApi newUserApi() {
        UserApi.Builder builder = new UserApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest httpRequest) {
                    }
                });
        return updateBuilder(builder).build();
    }

    public static EventApi newEventApi() {
        EventApi.Builder builder = new EventApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest httpRequest) {
                    }
                });
        return updateBuilder(builder).build();
    }

    public static PointApi newPointApi() {
        PointApi.Builder builder = new PointApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest httpRequest) {
                    }
                });
        return updateBuilder(builder).build();
    }

    public static EventParticipationApi newEventParticipationApi() {
        EventParticipationApi.Builder builder = new EventParticipationApi.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest httpRequest) {
                    }
                });
        return updateBuilder(builder).build();
    }

    private static <B extends AbstractGoogleClient.Builder> B updateBuilder(B builder) {
        if (LOCAL_ANDROID_RUN) {
            builder.setRootUrl(LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID + "/_ah/api/");
        }
        // only enable GZip when connecting to remote server
        final boolean enableGZip = builder.getRootUrl().startsWith("https:");

        builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                if (!enableGZip) {
                    request.setDisableGZipContent(true);
                }
            }
        });

        return builder;
    }

    public static User toEPAUser(pl.looter.appengine.domain.eventApi.model.User user) {
        return new User()
        .setId(user.getId())
        .setLastLogin(user.getLastLogin())
        .setLevel(user.getLevel())
        .setName(user.getName())
        .setRegistrationId(user.getRegistrationId())
        .setXp(user.getXp())
        .setRegistrationTime(user.getRegistrationTime());
    }

    public static User toEPAUser(pl.looter.appengine.userApi.model.User user) {
        return new User()
                .setId(user.getId())
                .setLastLogin(user.getLastLogin())
                .setLevel(user.getLevel())
                .setName(user.getName())
                .setRegistrationId(user.getRegistrationId())
                .setXp(user.getXp())
                .setRegistrationTime(user.getRegistrationTime());
    }

	public static pl.looter.appengine.domain.eventApi.model.User toEAUser(pl.looter.appengine.userApi.model.User user) {
		return new pl.looter.appengine.domain.eventApi.model.User()
				.setId(user.getId())
				.setLastLogin(user.getLastLogin())
				.setLevel(user.getLevel())
				.setName(user.getName())
				.setRegistrationId(user.getRegistrationId())
				.setXp(user.getXp())
				.setRegistrationTime(user.getRegistrationTime());
	}

	public static Event toEPAEvent(pl.looter.appengine.domain.eventApi.model.Event event) {
		return new Event()
				.setId(event.getId())
				.setTitle(event.getTitle())
				.setDescription(event.getDescription())
				.setDate(event.getDate())
				.setStartTime(event.getStartTime())
				.setEndTime(event.getEndTime())
				.setOpen(event.getOpen())
				.setMaster(toEPAUser(event.getMaster()));
	}
}
