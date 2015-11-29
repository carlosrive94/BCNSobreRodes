package jmcdw.bcnsobrerodes.Utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Joan on 26/11/2015.
 */
public class Path {
    private String mode;
    private List<HashMap<String,String>> polyline;
    private String ini_station = null;
    private String end_station = null;

    public Path(String mode, List<HashMap<String,String>> polyline, String ini_station, String end_station) {
        this.mode = mode;
        this.polyline = polyline;
        this.ini_station = ini_station;
        this.end_station = end_station;
    }

    public String getMode() {
        return mode;
    }

    public List<HashMap<String, String>> getPolyline() {
        return polyline;
    }

    public String getIni_station() {
        return ini_station;
    }

    public String getEnd_station() {
        return end_station;
    }

    public void setPolyline(List<HashMap<String, String>> polyline) {
        this.polyline = polyline;
    }
}
