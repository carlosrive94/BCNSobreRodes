package jmcdw.bcnsobrerodes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import jmcdw.bcnsobrerodes.Utils.PlacesFunctions;


public class PlacePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private Context context;
    private TextView mName;
    private TextView mAddress;
    private TextView mAttributions;
    private TextView mLocation;
    private PlacesFunctions placesFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        buildTexts();
        context = this;
        placesFunctions = new PlacesFunctions(this);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(placesFunctions.getBounds());
                Intent intent = null;
                try {
                    intent = intentBuilder.build(getApplicationContext());
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });

        //NOMES PER ENSENYAR LA FUNCIONALITAT
        final Button whereIam = (Button) findViewById(R.id.whereButton);
        whereIam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = placesFunctions.whereIam();
                mLocation.setText("Current location \n long: " + latLng.longitude +
                        " / lat: " + latLng.latitude);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, context);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null)
                attributions = "";

            mName.setText(name);
            mAddress.setText(address);
            mAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void buildTexts() {
        mName = (TextView) findViewById(R.id.textView);
        mAddress = (TextView) findViewById(R.id.textView2);
        mAttributions = (TextView) findViewById(R.id.textView3);
        mLocation = (TextView) findViewById(R.id.textView4);
    }

    public void onCategoriesNearby(View v) {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }
}
