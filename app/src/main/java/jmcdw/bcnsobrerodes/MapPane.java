package jmcdw.bcnsobrerodes;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

//import android.appwidget.;

public class MapPane extends Activity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Geocoder geocoder;
    private String infoToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pane);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng barcelona = new LatLng(41.3909267, 2.1673073);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 13));
        myMap = map;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.hasExtra("from") && data.hasExtra("to")) {
                //dibuixa la ruta des de "from" fins a "to"
                drawRoute(getIntent().getStringExtra("from"), getIntent().getStringExtra("to"));
            }
        }
    }

    protected void displayInfo() {
        TextView infoText = (TextView)findViewById(R.id.InfoText);
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
        EditText location_tf = (EditText)findViewById(R.id.AdressText);
        //afegeixo Barcelona al final del string per a que googleMaps no busqui a altres llocs.
        String location = location_tf.getText().toString() + ", Barcelona";
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            myMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
    }

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
                        //myMap.clear();
                        clearView();
                        EditText from_et = (EditText)((AlertDialog) dialog).findViewById(R.id.FromText);
                        EditText to_et = (EditText)((AlertDialog) dialog).findViewById(R.id.ToText);
                        String from = from_et.getText().toString();
                        String to = to_et.getText().toString();
                        drawRoute(from, to);
                        dialog.dismiss();
                        //esperem 1 segon a que el thread creat a drawRoute actualitzi la variable global infoToDisplay.
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 1s = 1000ms
                                displayInfo();
                            }
                        }, 1000);
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
    private void alertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("Avís");
        AlertDialog dialog = builder.create();
    }
    */

    public void drawRoute(String str_from, String str_to) {
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
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(latLng_from, latLng_to);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    /*

    Funcions i classes de rutes:

    */

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        //String sensor = "sensor=false";

        // Mode
        String mode = "mode="+"walking";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
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
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Error downloading url", e.toString());
        }finally{
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

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
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
                infoToDisplay = "Distància: "+distance+"\nTemps estimat: "+time;
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

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
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
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            myMap.addPolyline(lineOptions);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_pane, menu);
        return true;
    }*/
}
