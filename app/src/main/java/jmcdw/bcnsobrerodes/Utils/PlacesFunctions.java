package jmcdw.bcnsobrerodes.Utils;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.concurrent.ExecutionException;

public class PlacesFunctions implements GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int RADIUS_LOCATION = 50;
    private static Context context;
    private static GoogleApiClient mGoogleApiClient;
    CharSequence name;
    private LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(41.342535, 2.149677), new LatLng(41.745079, 2.167947));

    public PlacesFunctions(Context context) {
        this.context = context;
        buildGoogleAPIClient();
    }

    //Retorna -1 si el place amb id id no està a la bd
    public static String getPuntuacio(String id) throws ExecutionException, InterruptedException {
        String query = "select mobilitat from LocalitzacioMobilitat where idPlace=\"" + id + "\"";
        Persistence persistence = new Persistence(context);
        String res = persistence.execute(query, "select").get();
        if (res.equals("")) res = "-1";
        return res;
    }

    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage((AppCompatActivity) context, GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    public void loadNameOfPlace(String id) {
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, id);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                setName(places.get(0).getName());
            }
        });
    }

    public void setName(CharSequence name){
        this.name = name;
    }

    public CharSequence getName(){
        return name;
    }


    private LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }

    public LatLng whereIam() throws LocalitzacioDisabled {
        LatLng location = null;
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        if (currentLocation != null) {
            double longitude = currentLocation.getLongitude();
            double latitude = currentLocation.getLatitude();
            location = new LatLng(latitude, longitude);
        } else {
            throw new LocalitzacioDisabled();
        }
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

    public LatLngBounds getBounds() throws LocalitzacioDisabled {
        BOUNDS = convertCenterAndRadiusToBounds(whereIam(), RADIUS_LOCATION);
        return BOUNDS;
    }

    public void addPuntuacio(String id, float stars, int nPuntuacions) throws ExecutionException, InterruptedException {
        String query = "";
        String currentPuntuacio = getPuntuacio(id);
        nPuntuacions = ++nPuntuacions;
        if (!currentPuntuacio.equals("-1")) {
            float newStars = (Float.parseFloat(currentPuntuacio) + stars) / nPuntuacions;
            query = "update LocalitzacioMobilitat SET mobilitat=\"" + Float.toString(newStars) +
                    "\", nPuntuacions=\"" + nPuntuacions + "\" WHERE idPlace=\"" + id + "\"";
        } else {
            query = "insert into LocalitzacioMobilitat values(\"" + id + "\",\""
                    + Float.toString(stars) + "\", \"" + nPuntuacions + "\" )";
        }
        Persistence persistence = new Persistence(context);
        persistence.execute(query, "modification");

    }

    public int getNPuntuacions(String id) throws ExecutionException, InterruptedException {
        String query = "select nPuntuacions from LocalitzacioMobilitat where idPlace=\"" + id + "\"";
        Persistence persistence = new Persistence(context);
        String result = persistence.execute(query, "select").get();
        if (result.equals("")) return 0;
        else return Integer.parseInt(result);
    }

    public boolean esAccessible(String id) {
        if (Vars.ESTACIONS_NO_ACCESIBLES.contains(id)) return false;
        return true;
    }
}
