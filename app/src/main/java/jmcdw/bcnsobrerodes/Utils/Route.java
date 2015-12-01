package jmcdw.bcnsobrerodes.Utils;

import com.google.android.gms.maps.model.Polyline;

public class Route {
    private Polyline pol;
    private String dist;
    private String temps;

    public Route(String dist, String temps) {
        this.pol = null;
        this.dist = dist;
        this. temps = temps;
    }

    public Route(Polyline pol, String dist, String temps) {
        this.pol = pol;
        this.dist = dist;
        this. temps = temps;
    }

    public Polyline getPol() {
        return pol;
    }

    public void setPol(Polyline pol) {
        this.pol = pol;
    }

    public String getDist() {
        return dist;
    }

    public String getTemps() {
        return temps;
    }
}
