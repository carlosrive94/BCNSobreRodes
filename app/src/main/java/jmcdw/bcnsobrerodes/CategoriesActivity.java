package jmcdw.bcnsobrerodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

public class CategoriesActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = "CategoriesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Log.v(LOG_TAG, "After onCreate");
        buildGoogleAPIClient();
        Log.v(LOG_TAG, "After building GoogleAPIClient");
        if (mGoogleApiClient.isConnected()){
            Toast t = new Toast(this);
            t.setText("API Connected");
            t.show();
            Log.v(LOG_TAG, "Google API Connected");

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood: likelyPlaces){
                        Place place = placeLikelihood.getPlace();
                        TextView camp = (TextView) findViewById(R.id.byCategResult);
                        camp.setText("");
                        Log.v(LOG_TAG, place.getName().toString());
                        camp.append(place.getName().toString());
                    }
                }
            });

        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }
    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();
    }

    public void onClickOK(){
        EditText typeField = (EditText) findViewById(R.id.placeType);
        String gottenType = typeField.getText().toString();
        switch (gottenType){
            case "gym":

            case "supermarket":
        }
    }

}
