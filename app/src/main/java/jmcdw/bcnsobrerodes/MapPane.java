package jmcdw.bcnsobrerodes;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

//import android.appwidget.;

public class MapPane extends Activity implements OnMapReadyCallback {

    private GoogleMap myMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pane);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onSearch(View view) {
        EditText location_tf = (EditText)findViewById(R.id.AdressText);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            myMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
            myMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng barcelona = new LatLng(41.3909267,2.1673073);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 13));
        myMap = map;
    }
}
