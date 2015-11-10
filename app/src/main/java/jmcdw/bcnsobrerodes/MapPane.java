package jmcdw.bcnsobrerodes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

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

import jmcdw.bcnsobrerodes.Utils.Obstacle;
import jmcdw.bcnsobrerodes.Utils.ObstacleDatabaseStub;
import jmcdw.bcnsobrerodes.Utils.PlacesFunctions;

//import android.appwidget.;


public class MapPane extends AppCompatActivity implements OnMapReadyCallback, OnMapLongClickListener {

    private GoogleMap myMap;
    private Geocoder geocoder;
    private String infoToDisplay;
    //private GoogleApiClient myGoogleApiClient;
    private PlacesFunctions placesFunctions;
    private Context context;
    //private PolylineOptions myRuta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pane);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        context = this;
        placesFunctions = new PlacesFunctions(this);
        //myPlacesFunctions = new PlacesFunctions(this);
        //buildGoogleApiClient();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng barcelona = new LatLng(41.3909267, 2.1673073);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 13));
        myMap = map;
        myMap.setOnMapLongClickListener(this);
        //myMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng to) {
        myMap.clear();
        //obtain_my_location
        LatLng from = placesFunctions.whereIam();

        /*
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        StringBuilder sb = new StringBuilder();
        sb.append(address + "\n" + ", ");
        sb.append(postalCode + ", " + city + ", " + country);

        Marker mark = myMap.addMarker(new MarkerOptions()
                        .position(to)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(sb.toString())
        );
        mark.showInfoWindow();
        */

        //get info from locations
        Address address_from = getAddressFromLoc(from);
        Address address_to = getAddressFromLoc(to);
        String info_from = address_from.getAddressLine(0);
        String info_to = address_to.getAddressLine(0);

        drawRoute(from, info_from, to, info_to);
        //showRouteInfo();

    }
    /*
    @Override
    public void onMapClick(LatLng clickCoords) {

    }
    */
    //Pre: loc is not null
    public Address getAddressFromLoc(LatLng loc) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses.get(0);
    }

    /*protected synchronized void buildGoogleApiClient() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }*/

    protected void displayInfo() {
        TextView infoText = (TextView) findViewById(R.id.InfoText);
        infoText.setText(infoToDisplay);
    }

    protected void eraseDisplayedInfo() {
        infoToDisplay = "";
        displayInfo();
    }

    protected void clearView() {
        myMap.clear();
        eraseDisplayedInfo();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            myMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    public void onClickVeureObstacles(View view) {
        clearView();
        ObstacleDatabaseStub DB = new ObstacleDatabaseStub();
        ArrayList<Obstacle> obstacles = DB.getObstacles();
        for (Obstacle obstacle : obstacles) {
            myMap.addMarker(new MarkerOptions().position(obstacle.getPosicio()).title(obstacle.getDescripcio()));
        }
    }
    /*
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
    }*/

    public void onClickRoute(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_from_to, null))
                // Add action buttons
                .setPositiveButton("Cerca", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearView();
                        EditText from_et = (EditText) ((AlertDialog) dialog).findViewById(R.id.FromText);
                        EditText to_et = (EditText) ((AlertDialog) dialog).findViewById(R.id.ToText);
                        String from_str = from_et.getText().toString();
                        String to_str = to_et.getText().toString();

                        //obtenim la latitud i longitud dels punts de la ruta
                        List<Address> addressList = null;
                        Address addr_from = null;
                        Address addr_to = null;
                        if (from_str != null || !from_str.equals("")) {
                            try {
                                //afegim ", Barcelona" al final del string
                                addressList = geocoder.getFromLocationName(from_str + ", Barcelona", 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addr_from = addressList.get(0);
                        }
                        if (to_str != null || !to_str.equals("")) {
                            try {
                                //afegim ", Barcelona" al final del string
                                addressList = geocoder.getFromLocationName(to_str + ", Barcelona", 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addr_to = addressList.get(0);
                        }
                        LatLng from = new LatLng(addr_from.getLatitude(), addr_from.getLongitude());
                        LatLng to = new LatLng(addr_to.getLatitude(), addr_to.getLongitude());

                        //obtenim la informació dels punts de la ruta per mostrarlos als markers
                        String from_info = addr_from.getAddressLine(0);
                        String to_info = addr_to.getAddressLine(0);

                        drawRoute(from, from_info, to, to_info);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancela", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    /*
    private void showRouteInfo() {
        //esperem 1 segon a que el thread creat a drawRoute actualitzi la variable global infoToDisplay.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                displayInfo();
            }
        }, 1000);
    }*/


    private void alertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("Avís")
                .show()
                .closeOptionsMenu();
        AlertDialog dialog = builder.create();
    }


    /*public void drawRoute(String str_from, String str_to) {
        List<Address> addressList = null;
        Address addr_from = null;
        Address addr_to = null;
        if (str_from != null || !str_from.equals("")) {
            try {
                //afegim ", Barcelona" al final del string
                addressList = geocoder.getFromLocationName(str_from + ", Barcelona", 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addr_from = addressList.get(0);
        }
        if (str_to != null || !str_to.equals("")) {
            try {
                //afegim ", Barcelona" al final del string
                addressList = geocoder.getFromLocationName(str_to + ", Barcelona", 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addr_to = addressList.get(0);
        }
        LatLng latLng_from = new LatLng(addr_from.getLatitude(),addr_from.getLongitude());
        LatLng latLng_to = new LatLng(addr_to.getLatitude(),addr_to.getLongitude());
        myMap.addMarker(new MarkerOptions().position(latLng_from).title(addr_from.getAddressLine(0)));
        myMap.addMarker(new MarkerOptions().position(latLng_to).title(addr_to.getAddressLine(0)));
        LatLng cameraLatLng = new LatLng((latLng_from.latitude + latLng_to.latitude)/2,(latLng_from.longitude + latLng_to.longitude)/2);
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, 13));
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(latLng_from, latLng_to);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }*/

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
        String mode = "mode=" + "walking";

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
                final JSONArray legArray = parentArray.getJSONObject(0).getJSONArray("legs");
                JSONObject distanceObj = legArray.getJSONObject(0).getJSONObject("distance");
                JSONObject durationObj = legArray.getJSONObject(0).getJSONObject("duration");
                String distance = distanceObj.getString("text"); //String that contains the distance value formatted
                String time = durationObj.getString("text"); //String that contains the duration time value formatted
                infoToDisplay = "Distància: " + distance + "\nTemps estimat: " + time;
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
            displayInfo();
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

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
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

                // Drawing polyline in the Google Map for the i-th route
                myMap.addPolyline(lineOptions);

                //get obstacles de la BD i afegirlos a obstacles
                ObstacleDatabaseStub DB = new ObstacleDatabaseStub();
                ArrayList<Obstacle> obstacles = DB.getObstacles();

                ArrayList<Obstacle> obstaclesRuta = obstaclesARuta(lineOptions, obstacles);
                if (!obstaclesRuta.isEmpty()) {
                    alertDialog(obstaclesRuta.get(0).getDescripcio());
                }
            }
            //Save my route on global variables
            //myRuta = lineOptions;
            //41.3937473 , 2.1233227


        }
    }

    private ArrayList<Obstacle> obstaclesARuta(PolylineOptions lineOptions, ArrayList<Obstacle> obstacles) {
        ArrayList<Obstacle> obstaclesTrobats = new ArrayList<Obstacle>();
        for (Obstacle obstacle : obstacles) {
            LatLng punt = obstacle.getPosicio();
            for (LatLng polyCoords : lineOptions.getPoints()) {

                float[] results = new float[1];

                Location.distanceBetween(punt.latitude, punt.longitude,
                        polyCoords.latitude, polyCoords.longitude, results);

                if (results[0] < 20) {
                    obstaclesTrobats.add(obstacle);
                }
                break;
            }
        }
        return obstaclesTrobats;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_pane, menu);
        return true;
    }*/
}
