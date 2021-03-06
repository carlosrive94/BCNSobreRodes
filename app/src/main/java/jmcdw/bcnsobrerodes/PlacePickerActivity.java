package jmcdw.bcnsobrerodes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.LocalitzacioDisabled;
import jmcdw.bcnsobrerodes.Utils.PlacesFunctions;


public class PlacePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private Context context;
    private TextView mName, mAddress, mInfo;
    private PlacesFunctions placesFunctions;
    private String id;
    private RatingBar ratingBar;
    private int nPuntuacions;
    private RelativeLayout puntuaLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        initComponents();
        context = this;
        placesFunctions = new PlacesFunctions(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, context);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) attributions = "";
            id = place.getId();

            mName.setText(name);
            mAddress.setText(address);
            String info = "";

            if (placesFunctions.esAccessible(id)) {
                try {
                    nPuntuacions = placesFunctions.getNPuntuacions(id);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (nPuntuacions == 0) info += "\nNo ha estat puntuada mai.";
                else {
                    String puntuacio = null;
                    try {
                        puntuacio = placesFunctions.getPuntuacio(id);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ratingBar.setRating(Float.parseFloat(puntuacio));

                    info += "\nTé una puntuació d'accesibilitat de " + puntuacio + " estrelles.";
                    info += "\nHa estat puntuada " + nPuntuacions + " cops.";
                }
                puntuaLayout.setVisibility(View.VISIBLE);
            } else {
                puntuaLayout.setVisibility(View.INVISIBLE);
                info += "\nNo és accesible.";
            }
            mInfo.setText(info);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initComponents() {
        puntuaLayout = (RelativeLayout) findViewById(R.id.puntuaPlace);
        mName = (TextView) findViewById(R.id.namePlace);
        mAddress = (TextView) findViewById(R.id.addressPlace);
        mInfo = (TextView) findViewById(R.id.infoPlace);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                Intent intent = null;
                try {
                    intentBuilder.setLatLngBounds(placesFunctions.getBounds());
                    intent = intentBuilder.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (LocalitzacioDisabled localitzacioDisabled) {
                    Toast.makeText(context, "Localització no activada", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button puntua = (Button) findViewById(R.id.puntuaButton);
        puntua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float stars = ratingBar.getRating();
                try {
                    placesFunctions.addPuntuacio(id, stars, nPuntuacions);
                    Toast.makeText(context,
                            "La teva puntuacío ha estat guardada!", Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCategoriesNearby(View v) {
        Intent intent = new Intent(this, CategoriesActivity.class);
        startActivity(intent);
    }
}
