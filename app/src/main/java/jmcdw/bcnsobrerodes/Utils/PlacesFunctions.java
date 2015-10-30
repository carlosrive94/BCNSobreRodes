package jmcdw.bcnsobrerodes.Utils;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

public class PlacesFunctions implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int RADIUS_LOCATION = 50;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(41.342535, 2.149677), new LatLng(41.745079, 2.167947));
    private Context context;

    public PlacesFunctions(Context context) {
        this.context = context;
        buildGoogleAPIClient();
    }

    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage((AppCompatActivity) context, GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    private LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public LatLng whereIam() {
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        double longitude = currentLocation.getLongitude();
        double latitude = currentLocation.getLatitude();
        LatLng location = new LatLng(latitude, longitude);
        return location;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(context,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    public LatLngBounds getBounds() {
        BOUNDS = convertCenterAndRadiusToBounds(whereIam(), RADIUS_LOCATION);
        return BOUNDS;
    }
}
