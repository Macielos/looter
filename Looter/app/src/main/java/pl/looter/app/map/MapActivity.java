package pl.looter.app.map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.looter.R;
import pl.looter.app.dataholder.DataHolder;
import pl.looter.app.main.MainActivity;
import pl.looter.appengine.domain.eventApi.EventApi;
import pl.looter.appengine.domain.eventApi.model.Event;
import pl.looter.appengine.domain.pointApi.PointApi;
import pl.looter.appengine.domain.pointApi.model.GeoPt;
import pl.looter.appengine.domain.pointApi.model.Point;
import pl.looter.utils.Constants;
import pl.looter.utils.EndpointUtils;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener /* GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,*/ {

	private static final String TAG = MapActivity.class.getSimpleName();
	private static final int REQUEST_CODE = 1488;

	public enum MapMode {VIEW, CREATE_EVENT, SEARCH_FOR_EVENTS}

	@Bind(R.id.map_confirm_event)
	Button mapConfirmEvent;

	@Bind(R.id.map_back)
	Button mapBack;

	private GoogleMap map;

	private final EventApi eventApi = EndpointUtils.newEventApi();
	private final PointApi pointApi = EndpointUtils.newPointApi();

	private Map<String, Point> selectedPoints = new HashMap<>();
	private MapMode mapMode;
	private Marker selectedMarker;

	//private GoogleApiClient googleApiClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String modeFromIntent = getIntent().getStringExtra(Constants.MAP_MODE);
		mapMode = modeFromIntent == null ? MapMode.VIEW : MapMode.valueOf(modeFromIntent);

		setContentView(R.layout.activity_map);
		ButterKnife.bind(this);

		mapConfirmEvent.setVisibility(mapMode == MapMode.CREATE_EVENT ? View.VISIBLE : View.INVISIBLE);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
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

	/*
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();


		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}

		Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

		if (userLocation != null) {
			Log.i(TAG, "User location: " + userLocation);
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())));
		}

		setCameraTrackUserLocation();*/
	}

	@OnClick(R.id.map_confirm_event)
	public void onMapConfirmEventClick() {
		new AsyncTask<Void, Void, Event>() {

			@Override
			protected Event doInBackground(Void... params) {
				try {
					Event event = eventApi.insert(DataHolder.getInstance().getLastEvent()).execute();
					for(Point point: selectedPoints.values()) {
						pointApi.insert(point.setEventId(event.getId())).execute();
					}
					return event;
				} catch (IOException e) {
					Log.e(TAG, "Failed to insert event "+DataHolder.getInstance().getLastEvent()+" with points "+selectedPoints, e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Event event) {
				DataHolder.getInstance().setLastEvent(event);
			}
		}.execute();
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@OnClick(R.id.map_back)
	public void onMapBackClick() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	@Override
	public void onMapClick(LatLng latLng) {
		Log.i(TAG, "Click on map: " + latLng);
		Marker newMarker = map.addMarker(new MarkerOptions().title("Point " + (selectedPoints.size() + 1)).position(latLng).snippet("Click again to remove, click else to add chained point"));
		selectedPoints.put(newMarker.getId(), new Point().setGeoPoint(new GeoPt().setLatitude((float) latLng.latitude).setLongitude((float) latLng.longitude)).setNextPoint(getChainedPoint()));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.i(TAG, "Click on marker: " + marker);
		if (selectedMarker == marker) {
			Log.i(TAG, "Removing marker: " + marker);
			selectedPoints.remove(marker.getId());
			marker.remove();
			selectedMarker = null;
		} else {
			selectedMarker = marker;
		}
		return false;
	}

	private Point getChainedPoint() {
		return selectedMarker == null ? null : selectedPoints.get(selectedMarker.getId());
	}

/*
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					setCameraTrackUserLocation();
				} else {
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				}
		}
	}

	private void setCameraTrackUserLocation() throws SecurityException {
		map.setMyLocationEnabled(true);
		map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				if (location == null) {
					Log.i(TAG, "User location: null");
					return;
				}
				LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));

				Log.i(TAG, "User location: " + loc.toString());
			}
		});
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				googleApiClient, new LocationRequest(), this);

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "User location: " + location);
		LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
	}
*/
}
