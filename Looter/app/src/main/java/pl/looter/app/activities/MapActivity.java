package pl.looter.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.dataholder.EventStatusHolder;
import pl.looter.app.dialogs.DialogDisplayThread;
import pl.looter.app.dialogs.PointReachedDialog;
import pl.looter.appengine.domain.eventApi.EventApi;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.eventParticipationApi.EventParticipationApi;
import pl.looter.appengine.domain.eventParticipationApi.model.EventParticipation;
import pl.looter.appengine.domain.eventParticipationApi.model.EventStatus;
import pl.looter.appengine.domain.pointApi.PointApi;
import pl.looter.appengine.domain.pointApi.model.GeoPt;
import pl.looter.appengine.domain.pointApi.model.JsonMap;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.appengine.domain.pointApi.model.PointTreeCollection;
import pl.looter.appengine.userApi.model.User;
import pl.looter.utils.Constants;
import pl.looter.utils.EndpointUtils;
import pl.looter.utils.GameplayUtils;
import pl.looter.utils.TimeUtils;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = MapActivity.class.getSimpleName();

	public enum MapMode {EXPLORE, CREATE_EVENT, SEARCH_FOR_EVENTS}

	@Bind(R.id.map_confirm_event)
	Button mapConfirmEvent;
	@Bind(R.id.map_back)
	Button mapBack;
	@Bind(R.id.map_active_event)
	TextView mapActiveEvent;
	@Bind(R.id.map_current_location)
	TextView mapCurrentLocation;
	@Bind(R.id.map_track_my_location)
	CheckBox trackMyLocation;
	@Bind(R.id.map_connect_new_points)
	CheckBox connectNewPoints;

	private GoogleMap map;

	private final EventApi eventApi = EndpointUtils.newEventApi();
	private final PointApi pointApi = EndpointUtils.newPointApi();
	private final EventParticipationApi eventParticipationApi = EndpointUtils.newEventParticipationApi();

	private Map<String, Point> selectedPoints = new HashMap<>();

	private Map<String, Polyline> selectedPolylines = new HashMap<>();
	private Map<String, Polyline> selectedPolylines2 = new HashMap<>();

	private MapMode mapMode;
	private Marker selectedMarker;
	private PointTreeCollection pointTreeCollection = new PointTreeCollection().setTreePoints(new JsonMap()).setTrees(new JsonMap());

	private GoogleApiClient googleApiClient;

	private BlockingQueue<Point> visitedPointsToDisplayDialog = new ArrayBlockingQueue<>(Constants.QUEUE_SIZE);

	private DialogDisplayThread dialogDisplayThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String modeFromIntent = getIntent().getStringExtra(Constants.MAP_MODE);
		mapMode = modeFromIntent == null ? MapMode.EXPLORE : MapMode.valueOf(modeFromIntent);

		dialogDisplayThread = new DialogDisplayThread(this, visitedPointsToDisplayDialog);

		setContentView(R.layout.activity_map);
		ButterKnife.bind(this);

		mapConfirmEvent.setVisibility(mapMode == MapMode.CREATE_EVENT ? View.VISIBLE : View.INVISIBLE);
		connectNewPoints.setVisibility(mapMode == MapMode.CREATE_EVENT ? View.VISIBLE : View.INVISIBLE);
		trackMyLocation.setVisibility(mapMode == MapMode.EXPLORE ? View.VISIBLE : View.INVISIBLE);

		trackMyLocation.setChecked(mapMode == MapMode.EXPLORE);

		mapActiveEvent.setVisibility(mapMode != MapMode.CREATE_EVENT ? View.VISIBLE : View.INVISIBLE);
		mapActiveEvent.setText(getActiveEventText());
		mapCurrentLocation.setVisibility(mapMode != MapMode.CREATE_EVENT ? View.VISIBLE : View.INVISIBLE);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	private String getActiveEventText() {
		return getString(R.string.active_event)+" "+
				(DataHolder.getInstance().getActiveEventStatus() == null ? "NONE" : DataHolder.getInstance().getActiveEventStatus().getEventParticipation().getEvent().getTitle());
	}

	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;

		map.setOnMapClickListener(this);
		map.setOnMarkerClickListener(this);
		map.setOnMarkerDragListener(this);
		map.setOnMyLocationButtonClickListener(this);

		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();

		googleApiClient.connect();

	}

	@OnClick(R.id.map_confirm_event)
	public void onMapConfirmEventClick() {
		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.event_saving))
				.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onMapBackClick();
					}
				})
				.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
		new AsyncTask<Void, Void, Event>() {

			@Override
			protected Event doInBackground(Void... params) {
				try {
					Event event = eventApi.insert(DataHolder.getInstance().getLastEvent()).execute();
					for (Object o : pointTreeCollection.getTreePoints().values()) {
						Point point = (Point) o;
						point.setEventId(event.getId());
						//point.setRoot(pointTreeCollection.)
					}
					pointApi.insertTreeCollection(pointTreeCollection).execute();
					for (User user : DataHolder.getInstance().getLastEventInvitedUsers()) {
						eventParticipationApi.insert(
								new EventParticipation()
										.setMaster(EndpointUtils.toEPAUser(DataHolder.getInstance().getUser()))
										.setParticipant(EndpointUtils.toEPAUser(user))
										.setEvent(EndpointUtils.toEPAEvent(event))
										.setEventTime(event.getDate())
										.setSendTime(TimeUtils.now())
										.setStatus(Constants.INVITED)).execute();
					}
					return event;
				} catch (IOException e) {
					Log.e(TAG, "Failed to insert event " + DataHolder.getInstance().getLastEvent() + " with points " + selectedPoints, e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Event event) {
				if(event != null) {
					DataHolder.getInstance().setLastEvent(event);
					dialog.setTitle(R.string.event_saved);
				} else {
					dialog.setTitle(R.string.internal_error);
				}
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
			}
		}.execute();
	}

	@OnClick(R.id.map_back)
	public void onMapBackClick() {
		googleApiClient.disconnect();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onMapClick(LatLng latLng) {
		Log.i(TAG, "Click on map: " + latLng);
		if (mapMode == MapMode.CREATE_EVENT) {
			Marker newMarker = map.addMarker(new MarkerOptions().draggable(true).title("Point " + (selectedPoints.size() + 1)).position(latLng).snippet("Click again to remove, click else to add connected point"));
			Point newPoint = new Point().setGeoPoint(new GeoPt().setLatitude((float) latLng.latitude).setLongitude((float) latLng.longitude));
			selectedPoints.put(newMarker.getId(), newPoint);

			Point selectedPoint = getSelectedPoint();
			GameplayUtils.connectNode(pointTreeCollection, selectedPoint, newPoint, selectedMarker == null ? null : selectedMarker.getId(), newMarker.getId());
			if (selectedPoint != null) {
				putPolyline(map.addPolyline(new PolylineOptions().add(latLng, new LatLng(selectedPoint.getGeoPoint().getLatitude(), selectedPoint.getGeoPoint().getLongitude()))), selectedMarker.getId(), newMarker.getId());
			}
			if (connectNewPoints.isChecked()) {
				selectedMarker = newMarker;
			}
		}
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		selectedMarker = marker;
	}

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		Point selectedPoint = getSelectedPoint();
		if (selectedPoint != null) {
			selectedPoint.setGeoPoint(new GeoPt().setLatitude((float) marker.getPosition().latitude).setLongitude((float) marker.getPosition().longitude));
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		if(lastLocation != null) {
			LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			setLocationText(latLng);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.INITIAL_ZOOM));

			LocationServices.FusedLocationApi.requestLocationUpdates(
					googleApiClient, createLocationRequest(), this);
		}
	}

	private LocationRequest createLocationRequest() {
		return new LocationRequest()
				.setInterval(5000)
				.setFastestInterval(2500)
		        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
		if(trackMyLocation.isChecked()) {
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			setLocationText(latLng);
			map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			EventStatusHolder eventStatusHolder = DataHolder.getInstance().getActiveEventStatusHolder();
			if(eventStatusHolder != null) {
				Set<Point> pointsInRange = eventStatusHolder.processNewUserLocation(latLng);
				if(pointsInRange != null) {
					for(Point point: pointsInRange) {
						visitedPointsToDisplayDialog.add(point);
					}
				}
			}
			dialogDisplayThread.notifyAll();
		}
	}

	private void setLocationText(LatLng latLng) {
		mapCurrentLocation.setText(getString(R.string.map_current_location) + " at " + TimeUtils.formatDate(TimeUtils.now()) + ": " + latLng);
	}

	private void putPolyline(Polyline polyline, String id1, String id2) {
		selectedPolylines.put(id1, polyline);
		selectedPolylines2.put(id2, polyline);
	}

	private Polyline getPolyline(String id) {
		Polyline polyline = selectedPolylines.get(id);
		return polyline != null ? polyline : selectedPolylines2.get(id);
	}

	private void removePolyline(String id) {
		Polyline polyline = selectedPolylines.remove(id);
		if (polyline != null) {
			polyline.remove();
			selectedPolylines2.remove(id);
		} else {
			polyline = selectedPolylines2.remove(id);
			if (polyline != null) {
				polyline.remove();
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i(TAG, "Click on marker: " + marker);
		if (mapMode == MapMode.CREATE_EVENT && selectedMarker != null && marker.getId().equals(selectedMarker.getId())) {
			Log.i(TAG, "Removing marker: " + marker);
			selectedPoints.remove(marker.getId());
			boolean removed = GameplayUtils.removeNode(pointTreeCollection, marker.getId());
			if (removed) {
				marker.remove();
				removePolyline(marker.getId());
				selectedMarker = null;
			}
		} else {
			selectedMarker = marker;
		}
		return false;
	}

	private Point getSelectedPoint() {
		return selectedMarker == null ? null : selectedPoints.get(selectedMarker.getId());
	}

}
