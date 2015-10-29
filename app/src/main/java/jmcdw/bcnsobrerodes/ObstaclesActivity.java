package jmcdw.bcnsobrerodes;
import android.app.Activity;

import android.location.Geocoder;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

//import android.appwidget.;

public class ObstaclesActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Geocoder geocoder;
    private String infoToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obstacles);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapObstacles);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng barcelona = new LatLng(41.3909267, 2.1673073);
        TileProvider tileProvider = WMSFactory.getTileProvider();
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 13));
        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        myMap = map;
    }
}
