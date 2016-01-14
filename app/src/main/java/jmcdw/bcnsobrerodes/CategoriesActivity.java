package jmcdw.bcnsobrerodes;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class CategoriesActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_API_CLIENT_ID = 0;
    GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = "CategoriesActivity";
    private int mTipus = -1;
    private Spinner categSpinner;
    private TextView mfilterResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Log.v(LOG_TAG, "After onCreate");
        buildGoogleAPIClient();
        buildCategorySpinner();
        mfilterResult = (TextView) findViewById(R.id.byCategResult);
        mfilterResult.setTextColor(Color.parseColor("#1d318c"));
        mfilterResult.setTextSize(18);
        mfilterResult.setMovementMethod(new ScrollingMovementMethod());
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

    private void buildCategorySpinner(){
        categSpinner = (Spinner) findViewById(R.id.categorySelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categSpinner.setAdapter(adapter);
    }

    public void onClickOK(View v) {
        mfilterResult.setText("");
        if (mGoogleApiClient.isConnected()) {
            String gottenType = categSpinner.getSelectedItem().toString();
            switch (gottenType) {
                case "Gimnas":
                    mTipus = Place.TYPE_GYM;
                    break;
                case "Botiga":
                    mTipus = Place.TYPE_STORE;
                    break;
                case "Restaurant":
                    mTipus = Place.TYPE_RESTAURANT;
                    break;
                case "Hospital":
                    mTipus = Place.TYPE_HOSPITAL;
                    break;
                case "Banc":
                    mTipus = Place.TYPE_BANK;
                    break;
                case "Universitat":
                    mTipus = Place.TYPE_UNIVERSITY;
                    break;
                case "Escola":
                    mTipus = Place.TYPE_SCHOOL;
                    break;
                case "Totes":
                    break;
            }
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        Place place = placeLikelihood.getPlace();
                        if (place.getPlaceTypes().contains(mTipus) || mTipus == -1) {
                            Log.v(LOG_TAG, place.getName().toString());
                            mfilterResult.append(place.getName().toString());
                            mfilterResult.append("\n");
                            mTipus = -1;
                        }
                    }
                }
            });

        }
    }

}
