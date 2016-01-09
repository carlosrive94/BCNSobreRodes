package jmcdw.bcnsobrerodes;

import jmcdw.bcnsobrerodes.Utils.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;


public class DirectionsJSONParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<Path>> parse(JSONObject jObject){

        List<List<Path>> routes = new ArrayList<List<Path>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<Path> ruta = new ArrayList<Path>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    Path path = null;

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        String mode = "";
                        String ini_station = null;
                        String end_station = null;
                        Double ini_location_lat = null;
                        Double ini_location_lng = null;;
                        Double end_location_lat = null;
                        Double end_location_lng = null;
                        LatLng ini_location = null;
                        LatLng end_location = null;
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        //Si es transit s'ha d'agafar la estació origen i la estació desti
                        mode = (String)((JSONObject)((JSONObject)jSteps.get(k))).get("travel_mode");
                        if(mode.equals("TRANSIT")) {
                            ini_station = (String)((JSONObject)((JSONObject)((JSONObject)jSteps.get(k)).get("transit_details")).get("departure_stop")).get("name");
                            end_station = (String)((JSONObject)((JSONObject)((JSONObject)jSteps.get(k)).get("transit_details")).get("arrival_stop")).get("name");
                            ini_location_lat = (Double)((JSONObject)((JSONObject) ((JSONObject) ((JSONObject) jSteps.get(k)).get("transit_details")).get("departure_stop")).get("location")).get("lat");
                            ini_location_lng = (Double)((JSONObject)((JSONObject) ((JSONObject) ((JSONObject) jSteps.get(k)).get("transit_details")).get("departure_stop")).get("location")).get("lat");
                            ini_location = new LatLng(ini_location_lat,ini_location_lng);
                            end_location_lat = (Double)((JSONObject)((JSONObject) ((JSONObject) ((JSONObject) jSteps.get(k)).get("transit_details")).get("arrival_stop")).get("location")).get("lat");
                            end_location_lng = (Double)((JSONObject)((JSONObject) ((JSONObject) ((JSONObject) jSteps.get(k)).get("transit_details")).get("arrival_stop")).get("location")).get("lat");
                            end_location = new LatLng(end_location_lat,end_location_lng);
                        }
                        //Si es transit o driving no s'han de tenir en compte els obtacles que trobi

                        List<LatLng> list = decodePoly(polyline);
                        List<HashMap<String,String>> step = new ArrayList<>();
                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            step.add(hm);
                        }

                        path = new Path(mode, step , ini_station, end_station, ini_location, end_location);
                        ruta.add(path);
                    }
                }
                routes.add(ruta);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}