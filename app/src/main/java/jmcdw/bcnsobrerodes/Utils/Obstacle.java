package jmcdw.bcnsobrerodes.Utils;

import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
    private LatLng posicio;
    private String descripcio;

    public Obstacle(LatLng posicio, String descripcio) {
        this.posicio = posicio;
        this.descripcio = descripcio;
    }

    public LatLng getPosicio() {
        return posicio;
    }

    public String getDescripcio() {
        return descripcio;
    }
}
