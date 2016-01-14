package jmcdw.bcnsobrerodes.Utils;

import com.google.android.gms.maps.model.LatLng;

public class Obstacle {
    private LatLng posicio;
    private String descripcio;
    private Boolean verificat;

    public Obstacle(LatLng posicio, String descripcio) {
        this.posicio = posicio;
        this.descripcio = descripcio;
    }

    public Obstacle(LatLng posicio, String descripcio, Boolean verificat) {
        this.posicio = posicio;
        this.descripcio = descripcio;
        this.verificat = verificat;
    }

    public LatLng getPosicio() {
        return posicio;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public Boolean esVerificat() {
        return verificat;
    }
}
