package jmcdw.bcnsobrerodes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.Vector;

import jmcdw.bcnsobrerodes.Utils.LocalitzacioDisabled;
import jmcdw.bcnsobrerodes.Utils.Obstacle;
import jmcdw.bcnsobrerodes.Utils.Persistence;
import jmcdw.bcnsobrerodes.Utils.PlacesFunctions;
import jmcdw.bcnsobrerodes.Utils.Path;
import jmcdw.bcnsobrerodes.Utils.Route;
import jmcdw.bcnsobrerodes.Utils.Vars;

public class MapPane extends AppCompatActivity implements OnMapReadyCallback, OnMapLongClickListener, OnMapClickListener {
    private GoogleMap myMap;
    private Geocoder geocoder;
    private PlacesFunctions placesFunctions;
    private Context context;
    private boolean obstaclesMostrats;
    private ArrayList<Marker> markersObstacles;
    private ArrayList<Obstacle> obstaclesDB;
    private String travelMode;
    private SharedPreferences sp;
    private String username;
    private String clickedAddress;
    private boolean enableRouteClick;
    private List<Route> myRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pane);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        context = this;
        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sp.getString("username", null);
        placesFunctions = new PlacesFunctions(this);
        obstaclesMostrats = true;
        markersObstacles = new ArrayList<>();
        obstaclesDB = new ArrayList<>();
        clickedAddress = "";
        enableRouteClick = false;
        myRoutes = new Vector<>();
    }

    public void carregaObstaclesDB() {
        String query = "select latitud,longitud,descripcio,verificat from Obstacles";
        Persistence persistence = new Persistence(this);
        try {
            String rows[] = persistence.execute(query, "select").get().split("/");
            for (String row : rows) {
                String[] infoObstacle = row.split("-");
                double lat = Double.parseDouble(infoObstacle[0]);
                double lng = Double.parseDouble(infoObstacle[1]);
                LatLng loc = new LatLng(lat,lng);
                String desc = infoObstacle[2];
                Boolean verif = (Integer.parseInt(infoObstacle[3]) == 1);
                obstaclesDB.add(new Obstacle(loc,desc,verif));
            }
        } catch (Exception e) {
            alertDialog(e.getMessage());
        }
    }

    public void carregaMarkersObstacles() {
        for (Obstacle obstacle : obstaclesDB) {
            try {
                Address addr = getAddressFromLoc(obstacle.getPosicio());
                String obstacleAddress = addr.getAddressLine(0);
                float alpha = 1;
                if (!obstacle.esVerificat()) alpha = 0.5f;
                markersObstacles.add(myMap.addMarker(new MarkerOptions()
                        .position(obstacle.getPosicio())
                        .title(obstacleAddress)
                        .snippet(obstacle.getDescripcio())
                        .alpha(alpha)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
            }

            catch (IOException e) {
                alertDialog(e.getMessage());
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng barcelona = new LatLng(41.3909267, 2.1673073);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 13));
        myMap = map;
        myMap.setOnMapLongClickListener(this);
        myMap.setOnMapClickListener(this);
        carregaObstaclesDB();
        carregaMarkersObstacles();
    }

    private Route clickedRoute(LatLng clicked_place) {

        for (Route route : myRoutes) {
            Polyline pol = route.getPol();
            try {
                if (PolyUtil.isLocationOnPath(clicked_place, pol.getPoints(), true, 70)) {
                    return route;
                }
            }
            catch (NullPointerException e ) {

            }
        }
        return null;
    }

    @Override
    public void onMapClick(LatLng clicked_place) {
        if (enableRouteClick) {
            Route clickedRoute = clickedRoute(clicked_place);
            if (clickedRoute != null && clickedRoute.getPol() != null) {
                Polyline clickedRoutePol = clickedRoute.getPol();
                if (clickedRoutePol.getColor() == Color.GRAY) {
                    //get old selected route
                    Route oldSelectedRoute = null;
                    for (Route route : myRoutes) {
                        if (route.getPol().getColor() != Color.GRAY) {
                            oldSelectedRoute = route;
                            break;
                        }
                    }

                    //prepare polylineOpts for the new selected route
                    PolylineOptions newSelectedRouteOpts = new PolylineOptions();
                    newSelectedRouteOpts.addAll(clickedRoutePol.getPoints());
                    newSelectedRouteOpts.width(4);
                    newSelectedRouteOpts.zIndex(2);
                    if (clickedRoute.esAccessible()) {
                        newSelectedRouteOpts.color(Color.BLUE);
                    }
                    else {
                        newSelectedRouteOpts.color(Color.RED);
                    }

                    //update clicked polyline (make it blue) and display new selected route info
                    clickedRoutePol.remove();
                    clickedRoute.setPol(myMap.addPolyline(newSelectedRouteOpts));
                    displayInfo("Ruta "+(myRoutes.indexOf(clickedRoute)+1)+"\nDistància: " + clickedRoute.getDist() + "\nTemps estimat: " + clickedRoute.getTemps());

                    //prepare polylineOpts for the old selected route
                    PolylineOptions auxPolOpts = new PolylineOptions();
                    auxPolOpts.addAll(oldSelectedRoute.getPol().getPoints());
                    auxPolOpts.width(4);
                    auxPolOpts.zIndex(1);
                    auxPolOpts.color(Color.GRAY);

                    //update old selected route polyline (make it gray)
                    Polyline oldSelectedPol = oldSelectedRoute.getPol();
                    oldSelectedPol.remove();
                    oldSelectedRoute.setPol(myMap.addPolyline(auxPolOpts));
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng clicked_place) {
        clearView();
        //obtain_my_location
        try {
            Address address = getAddressFromLoc(clicked_place);
            clickedAddress = address.getAddressLine(0);
            myMap.addMarker(new MarkerOptions().position(clicked_place).title(clickedAddress));
            myMap.animateCamera(CameraUpdateFactory.newLatLng(clicked_place));
        }
        catch (IOException e) {
            alertDialog(e.getMessage());
        }
    }

    //Pre: loc is not null
    public Address getAddressFromLoc(LatLng loc) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        return addresses.get(0);
    }

    protected void displayInfo(String info) {
        TextView infoText = (TextView) findViewById(R.id.InfoText);
        infoText.setText(info);
    }

    protected void eraseDisplayedInfo() {
        displayInfo("");
    }

    protected void clearView() {
        myMap.clear();
        enableRouteClick = false;
        eraseDisplayedInfo();
        clickedAddress = "";
        ArrayList<Marker> aux = new ArrayList<>();
        for (Marker marker : markersObstacles) {
            aux.add(myMap.addMarker(new MarkerOptions()
                    .position(marker.getPosition())
                    .title(marker.getTitle())
                    .snippet(marker.getSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
        }
        markersObstacles = aux;
        if(!obstaclesMostrats) {
            for(Marker marker: markersObstacles) {
                marker.setVisible(false);
            }
        }
        //natejo la variable global myRoutes
        for (int i = 0; i < myRoutes.size(); i++) {
            myRoutes.remove(i);
        }
    }

    public void onClickSearch(View view) {
        clearView();
        EditText location_tf = (EditText) findViewById(R.id.AdressText);
        //afegeixo Barcelona al final del string per a que googleMaps no busqui a altres llocs.
        String location = location_tf.getText().toString() + ", Barcelona";
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                myMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } catch (Exception e) {
                alertDialog(e.getMessage());
            }
        }
    }

    //LocalitzacioDisabled => localització no activada, IOException => error del Geocoder
    private String getMyLocationAddress() throws LocalitzacioDisabled, IOException {
        String res = "";
        boolean locationEnabled = true;
        LatLng loc = placesFunctions.whereIam();
        Address address_loc = getAddressFromLoc(loc);
        res = address_loc.getAddressLine(0);
        return res;
    }

    public void onClickObstaclesButton(View view) {
        Button ObstaclesButton = (Button) findViewById(R.id.ObstacleButton);
        if (!obstaclesMostrats) {
            for (Marker marker : markersObstacles) {
                marker.setVisible(true);
            }
            ObstaclesButton.setText("Amaga Obstacles");
            obstaclesMostrats = true;
        }
        else {
            for (Marker marker : markersObstacles) {
                marker.setVisible(false);
            }
            ObstaclesButton.setText("Mostra Obstacles");
            obstaclesMostrats = false;
        }
    }

    public void onClickRoute(View view) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_from_to);
        dialog.setTitle("Ruta");

        RadioButton walking_rbtn = (RadioButton) dialog.findViewById(R.id.rbtn_walking);
        walking_rbtn.setChecked(true);

        if(clickedAddress != "") {
            try {
                String my_location = getMyLocationAddress();
                if (my_location != "") {
                    EditText from_txt = (EditText) dialog.findViewById(R.id.FromText);
                    from_txt.setText(my_location);
                }
                EditText to_txt = (EditText) dialog.findViewById(R.id.ToText);
                to_txt.setText(clickedAddress);
            }
            catch (LocalitzacioDisabled l) {
                alertDialog("Per usar aquesta funcionalitat cal activar la localització");
            }
            catch (IOException e) {
                alertDialog(e.getMessage());
            }

        }

        Button loc_from = (Button) dialog.findViewById(R.id.btn_LocFrom);
        Button loc_to = (Button) dialog.findViewById(R.id.btn_LocTo);
        Button close = (Button) dialog.findViewById(R.id.btn_close);
        Button search = (Button) dialog.findViewById(R.id.btn_search);
        // if button is clicked, close the custom dialog
        loc_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText from_text = (EditText) dialog.findViewById(R.id.FromText);
                try {
                    String my_location = getMyLocationAddress();
                    from_text.setText(my_location);
                }
                catch (LocalitzacioDisabled l) {
                    alertDialog("Per usar aquesta funcionalitat has d'activar la localització");
                }
                catch (IOException e) {
                    alertDialog(e.getMessage());
                }
            }
        });

        loc_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText from_text = (EditText) dialog.findViewById(R.id.ToText);
                try {
                    String my_location = getMyLocationAddress();
                    from_text.setText(my_location);
                }
                catch (LocalitzacioDisabled l){
                    alertDialog("Per usar aquesta funcionalitat has d'activar la localització");
                }
                catch (IOException e) {
                    alertDialog(e.getMessage());
                }
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearView();
                EditText from_et = (EditText) dialog.findViewById(R.id.FromText);
                EditText to_et = (EditText) dialog.findViewById(R.id.ToText);
                String from_str = from_et.getText().toString();
                String to_str = to_et.getText().toString();

                //obtenim la latitud i longitud dels punts de la ruta
                List<Address> addressList = null;
                Address addr_from = null;
                Address addr_to = null;
                //aquestes línies son necessàries per evitar un error de la api de Google Maps en buscar el LatLng del Carrer Santa Maria Cervelló
                if (from_str.equals("Carrer Santa Maria Cervelló")) from_str = "Carrer del Gran Capità, 6";
                if (to_str.equals("Carrer Santa Maria Cervelló")) from_str = "Carrer del Gran Capità, 6";
                if (from_str != null || !from_str.equals("")) {
                    try {
                        //afegim ", Barcelona" al final del string
                        addressList = geocoder.getFromLocationName(from_str + ", Barcelona", 1);
                    } catch (IOException e) {
                        alertDialog(e.getMessage());
                        return;
                    }
                    addr_from = addressList.get(0);
                }
                if (to_str != null || !to_str.equals("")) {
                    try {
                        //afegim ", Barcelona" al final del string
                        addressList = geocoder.getFromLocationName(to_str + ", Barcelona", 1);
                    } catch (IOException e) {
                        alertDialog(e.getMessage());
                        return;
                    }
                    addr_to = addressList.get(0);
                }
                LatLng from = new LatLng(addr_from.getLatitude(), addr_from.getLongitude());
                LatLng to = new LatLng(addr_to.getLatitude(), addr_to.getLongitude());

                //obtenim la informació dels punts de la ruta per mostrarlos als markers
                String from_info = addr_from.getAddressLine(0);
                String to_info = addr_to.getAddressLine(0);
                //get travelMode from radioGroup
                RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.OptionsRadioGroup);
                int checkedId = radioGroup.getCheckedRadioButtonId();
                if (checkedId == R.id.rbtn_walking) {
                    travelMode = "walking";
                }
                else if (checkedId == R.id.rbtn_driving) {
                    travelMode = "driving";
                }
                else if (checkedId == R.id.rbtn_public_transport) {
                    travelMode = "transit";
                }
                else {
                    travelMode = "walking";
                }
                drawRoute(from, from_info, to, to_info);
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void alertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("Avís")
                .setNegativeButton("Tanca", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public void drawRoute(LatLng from, String from_info, LatLng to, String to_info) {
        myMap.addMarker(new MarkerOptions().position(from).title(from_info));
        myMap.addMarker(new MarkerOptions().position(to).title(to_info));
        //col·loquem la càmera al mig de la ruta
        LatLng cameraLatLng = new LatLng((from.latitude + to.latitude) / 2, (from.longitude + to.longitude) / 2);
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, 13));
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(from, to);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    /*

    Funcions i classes de rutes:

    */

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        //String sensor = "sensor=false";

        // Provide route alternatives
        String route_alternatives = "alternatives=" + "true";

        // Mode
        String mode = "mode=" + travelMode;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + route_alternatives;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            //display distance and travel duration values
            try {
                JSONObject jsonObj = new JSONObject(data.toString());
                JSONArray parentArray = jsonObj.getJSONArray("routes");
                //Log.d("#rutes", Integer.toString(parentArray.length()));
                for (int i=0; i<parentArray.length(); i++) {
                    JSONArray legArray = parentArray.getJSONObject(i).getJSONArray("legs");
                    JSONObject distanceObj = legArray.getJSONObject(0).getJSONObject("distance");
                    JSONObject durationObj = legArray.getJSONObject(0).getJSONObject("duration");
                    String distance = distanceObj.getString("text"); //String that contains the distance value formatted
                    String time = durationObj.getString("text"); //String that contains the duration time value formatted
                    Route ruta = new Route(distance,time);
                    myRoutes.add(ruta);
                }
                /*if (parentArray.length()>1) {
                    Log.d("Ruta1 distance", myRoutes.get(0).getDist());
                    Log.d("Ruta2 distance", myRoutes.get(1).getDist());
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<Path>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<Path>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<Path>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<Path>> result) {
            boolean mostratAvis = false;
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            ArrayList<MarkerOptions> station_markers = new ArrayList<>();
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                Boolean accesible = true;
                List<String> stations = new ArrayList<String>();

                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<Path> path = result.get(i);

                //Fetching all the steps in i-th route
                for(int j = 0; j < path.size(); j++) {
                    //Fetching the j-th step
                    Path step = path.get(j);
                    if(step.getMode().equals("TRANSIT")) {
                        if(Vars.NAME_ESTACIONS_NO_ACCESIBLES.contains(step.getIni_station())) {
                            accesible = false;
                            stations.add(step.getIni_station());
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.senyalpeligro);
                            Boolean printedStation = false;
                            for (int q=0; q<station_markers.size(); q++) {
                                if (station_markers.get(q).getTitle().equals(step.getIni_station())) {
                                    printedStation = true;
                                    if (!station_markers.get(q).getSnippet().contains(String.valueOf(i))) {
                                        station_markers.get(q).snippet(station_markers.get(q).getSnippet()+","+i);
                                        break;
                                    }
                                }
                            }
                            if (!printedStation) {
                                MarkerOptions opts = new MarkerOptions();
                                opts.position(step.getIni_location());
                                opts.title(step.getIni_station());
                                opts.icon(icon);
                                opts.snippet("Ruta "+i);
                                station_markers.add(opts);
                            }
                        }
                        else {
                            Boolean printedStation = false;
                            for (int q=0; q<station_markers.size(); q++) {
                                if (station_markers.get(q).getTitle().equals(step.getIni_station())) {
                                    printedStation = true;
                                    if (!station_markers.get(q).getSnippet().contains(String.valueOf(i))) {
                                        station_markers.get(q).snippet(station_markers.get(q).getSnippet()+","+i);
                                        break;
                                    }
                                }
                            }
                            if (!printedStation) {
                                MarkerOptions opts = new MarkerOptions();
                                opts.position(step.getIni_location());
                                opts.title(step.getIni_station());
                                opts.snippet("Ruta " +i);
                                opts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                station_markers.add(opts);
                            }
                        }
                        if(Vars.NAME_ESTACIONS_NO_ACCESIBLES.contains(step.getEnd_station())) {
                            accesible = false;
                            stations.add(step.getEnd_station());
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.senyalpeligro);
                            Boolean printedStation = false;
                            for (int q=0; q<station_markers.size(); q++) {
                                if (station_markers.get(q).getTitle().equals(step.getEnd_station())) {
                                    printedStation = true;
                                    if (!station_markers.get(q).getSnippet().contains(String.valueOf(i))) {
                                        station_markers.get(q).snippet(station_markers.get(q).getSnippet()+","+i);
                                        break;
                                    }
                                }
                            }
                            if (!printedStation) {
                                MarkerOptions opts = new MarkerOptions();
                                opts.position(step.getEnd_location());
                                opts.title(step.getEnd_station());
                                opts.snippet("Ruta " + i);
                                opts.icon(icon);
                                station_markers.add(opts);
                            }
                        }
                        else {
                            Boolean printedStation = false;
                            for (int q=0; q<station_markers.size(); q++) {
                                if (station_markers.get(q).getTitle().equals(step.getEnd_station())) {
                                    printedStation = true;
                                    if (!station_markers.get(q).getSnippet().contains(String.valueOf(i))) {
                                        station_markers.get(q).snippet(station_markers.get(q).getSnippet()+","+i);
                                        break;
                                    }
                                }
                            }
                            if (!printedStation) {
                                MarkerOptions opts = new MarkerOptions();
                                opts.position(step.getEnd_location());
                                opts.title(step.getEnd_station());
                                opts.snippet("Ruta "+i);
                                opts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                station_markers.add(opts);
                            }
                        }
                        // per tal que sigui una estació no adaptada s'ha de trobar la estació_ini
                        // o fi dins de la base de dades de estacions no adaptades
                        // Si la estació no es adaptada posar un flag
                    }


                    List<HashMap<String, String>> lineStep = step.getPolyline();
                    // Fetching all the points in j-th step
                    for (int k = 0; k < lineStep.size(); k++) {
                        HashMap<String, String> point = lineStep.get(k);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);

                // Si es TRANSIT
                // i no és adaptada es busca una alternativa
                //      Si la estació_ini no es adaptada es busca la estació més a prop
                //      de estació_ini
                //      Si la estació_fi no es adaptada es busca la estació més a prop
                //      de estació_fi
                //          Es calcula una nova ruta que pasi pels markers de les estacions que s'han calculat anteriormen

                if (travelMode.equals("transit")) {
                    if (!accesible) {
                        //Mostra avís estació no accesible i es calculen rutes alternatives
                        if (!mostratAvis) {
                            String info = "La estació ";
                            Boolean un = true;
                            for (String station : stations) {
                                if (!un) info += " i ";
                                info += station;
                                un = false;
                            }
                            info += " no és accessible.";
                            alertDialog(info);
                            mostratAvis = true;
                        }
                        myRoutes.get(i).setAccessible(false);
                    }
                    else {
                        myRoutes.get(i).setAccessible(true);
                    }
                    enableRouteClick = true;
                }

                //get obstacles de la BD i afegirlos a obstacles
                //si es WALKING
                else if(travelMode.equals("walking")) {
                    if (!mostratAvis) {
                        ArrayList<Obstacle> obstaclesRuta = obteObstaclesARuta(lineOptions, obstaclesDB);
                        if (obstaclesRuta.isEmpty()) {
                            myRoutes.get(i).setAccessible(true);
                        } else {
                            myRoutes.get(i).setAccessible(false);
                            String info = "Hem tobat " + obstaclesRuta.size();
                            if (obstaclesRuta.size() == 1)
                                info += " possible obstacle a la ruta " + (i+1) + ":\n";
                            else info += " possibles obstacles a la ruta " + (i+1) +":\n";
                            for (Obstacle obstacle : obstaclesRuta) {
                                info += "- " + obstacle.getDescripcio() + "\n";
                            }
                            alertDialog(info);
                            mostratAvis = true;
                        }
                    } else {
                        ArrayList<Obstacle> obstaclesRuta = obteObstaclesARuta(lineOptions, obstaclesDB);
                        if (!obstaclesRuta.isEmpty()) {
                            myRoutes.get(i).setAccessible(false);
                        }
                    }
                    enableRouteClick = true;
                }
                else {
                    myRoutes.get(i).setAccessible(true);
                    enableRouteClick = true;
                }

                if (i == 0) {
                    //és la ruta principal
                    if (myRoutes.get(i).esAccessible() == true) { lineOptions.color(Color.BLUE); }
                    else { lineOptions.color(Color.RED); }
                    lineOptions.zIndex(2);
                }
                else {
                    //és una ruta alternativa
                    lineOptions.color(Color.GRAY);
                    lineOptions.zIndex(1);
                }
                myRoutes.get(i).setPol(myMap.addPolyline(lineOptions));
            }
            //mostro la info de la ruta principal
            displayInfo("Ruta 1\nDistància: " + myRoutes.get(0).getDist() + "\nTemps estimat: " + myRoutes.get(0).getTemps());
            //pinto els markers de les estacions si cal
            if(travelMode.equals("transit")) {
                for (MarkerOptions markerOpts : station_markers) {
                    myMap.addMarker(markerOpts);
                }
            }
        }
    }

    private ArrayList<Obstacle> obteObstaclesARuta(PolylineOptions lineOptions, ArrayList<Obstacle> obstacles) {
        ArrayList<Obstacle> obstaclesTrobats = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            LatLng punt = obstacle.getPosicio();
            if (PolyUtil.isLocationOnPath(punt, lineOptions.getPoints(), true, 20)) {
                obstaclesTrobats.add(obstacle);
            }
        }
        return obstaclesTrobats;
    }

    public void onClickIncidenciaButton(View view) {
        //obrir pop-up incidencia
        final String verifBanned = "select esta_baneado from users " +
                "where username = \""+ username + "\"";

        Persistence persistence = new Persistence(this);
        String result = "";
        try {
            result = persistence.execute(verifBanned, "select").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
       if (result.equals("0")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final AlertDialog.Builder builder1 = builder.setView(inflater.inflate(R.layout.dialog_incidencia, null))
                    // Add action buttons
                    .setPositiveButton("Crear Incidència", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //clearView();
                            EditText comentariText = (EditText) ((AlertDialog) dialog).findViewById(R.id.Comentari);
                            String comentari = comentariText.getText().toString();
                            LatLng pos = null;
                            //sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            //username = sp.getString("username", null);

                            if(comentari.replaceAll("\\s","")=="" || comentari == null) {
                                alertDialog("És obligatori escriure un comentari");
                            }
                            else{
                                try {
                                    pos = placesFunctions.whereIam();
                                    String query = "insert into Obstacles(latitud, longitud, descripcio, afegit_per)" +
                                            " values(\"" + pos.latitude + "\", \"" + pos.longitude + "\", \"" +
                                            comentari + "\", \"" + username + "\")";
                                    Persistence persistence = new Persistence(context);
                                    try {
                                        persistence.execute(query, "modification");
                                    } catch (Exception e) {
                                        alertDialog(e.getMessage());
                                    }
                                    Obstacle obstacle = new Obstacle(pos, comentari, false);
                                    try {
                                        String obstacleAddress = getAddressFromLoc(obstacle.getPosicio()).getAddressLine(0);
                                        markersObstacles.add(myMap.addMarker(new MarkerOptions()
                                                .position(obstacle.getPosicio())
                                                .title(obstacleAddress)
                                                .snippet(obstacle.getDescripcio())
                                                .alpha(0.5f)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
                                        obstaclesDB.add(obstacle);
                                    }
                                    catch (IOException e) {
                                        Toast.makeText(MapPane.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (LocalitzacioDisabled localitzacioDisabled) {
                                    alertDialog("Per usar aquesta funcionalitat has d'activar la localització");
                                }
                            }
                        }
                    })
                    .setNegativeButton("Cancela", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
        else {
            Toast.makeText(MapPane.this, "Ho sentim, el teu usari està banejat", Toast.LENGTH_LONG).show();
        }
    }
}
