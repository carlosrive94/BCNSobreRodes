package jmcdw.bcnsobrerodes.Utils;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.location.Geocoder;

import jmcdw.bcnsobrerodes.MapPane;

public class ObstacleDatabaseStub extends MapPane{
    private ArrayList<Obstacle> obstacles;
    private Geocoder geocoder;

    public ObstacleDatabaseStub() {
        this.obstacles = new ArrayList<Obstacle>();
        geocoder = new Geocoder(this);

        Obstacle obs1 = new Obstacle(getLatLng("carrer de santa amelia"),"escales");
        Obstacle obs2 = new Obstacle(getLatLng("carrer folgueroles 17"),"voreres estretes");
        Obstacle obs3 = new Obstacle(getLatLng("carrer diagonal 150"),"meteorit");
        Obstacle obs4 = new Obstacle(getLatLng("carrer brusi"),"obres");

        obstacles = new ArrayList<Obstacle>();
        obstacles.add(obs1);
        obstacles.add(obs2);
        obstacles.add(obs3);
        obstacles.add(obs4);
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    private LatLng getLatLng(String location) {
        //afegeixo Barcelona al final del string per a que googleMaps no busqui a altres llocs.
        location +=  ", Barcelona";
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            return latLng;
        }
        return null;
    }
}
