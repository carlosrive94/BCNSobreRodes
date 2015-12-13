package jmcdw.bcnsobrerodes.Utils;

import com.google.android.gms.maps.model.Polyline;

public class Route {
    private Polyline pol;
    private String dist;
    private String temps;
    private Boolean accessible;

    public Route(String dist, String temps) {
        this.pol = null;
        this.dist = dist;
        this.temps = temps;
        this.accessible = true;
    }

    public Route(Polyline pol, String dist, String temps, Boolean accessible) {
        this.pol = pol;
        this.dist = dist;
        this.temps = temps;
        this.accessible = accessible;
    }

    public Polyline getPol() {
        return pol;
    }

    public void setPol(Polyline pol) {
        this.pol = pol;
    }

    public void setAccessible(Boolean accessible) { this.accessible = accessible; }

    public String getDist() {
        return dist;
    }

    public String getTemps() {
        return temps;
    }

    public Boolean esAccessible() { return accessible; }
}
