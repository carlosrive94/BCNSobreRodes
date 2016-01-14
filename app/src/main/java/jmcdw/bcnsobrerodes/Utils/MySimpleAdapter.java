package jmcdw.bcnsobrerodes.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jmcdw.bcnsobrerodes.R;
import jmcdw.bcnsobrerodes.Utils.Persistence;
import jmcdw.bcnsobrerodes.Verify;

/**
 * Created by ywy on 2015/11/21.
 */
public class MySimpleAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, Object>> list;
    private int layoutID;
    private String flag[];
    private int ItemIDs[];
    private int auxposition;
    private Context context;

    public MySimpleAdapter(Context context, List<Map<String, Object>> list,
                           int layoutID, String flag[], int ItemIDs[]) {

        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.layoutID = layoutID;
        this.flag = flag;
        this.ItemIDs = ItemIDs;
        this.auxposition = 0;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return 0;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        auxposition = position;
        convertView = mInflater.inflate(layoutID, null);
        for (int i = 0; i < flag.length-1; i++) {
            if (convertView.findViewById(ItemIDs[i]) instanceof TextView) {

                TextView tv = (TextView) convertView.findViewById(ItemIDs[i]);

                tv.setText((String) list.get(position).get(flag[i]));
            }
        }

        ((Button)convertView.findViewById(R.id.Approvebtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = (String) list.get(position).get(flag[0]);
                        String[] latlong =  ((String) list.get(position).get(flag[2])).split(",");
                        double lat = Double.parseDouble(latlong[0]);
                        double lng = Double.parseDouble(latlong[1]);

                        String query = "update Obstacles " +
                                "set verificat = 1 " +
                                "where afegit_per =\""+username+"\" AND longitud =\""+lng+"\" AND latitud =\""+lat+"\"";

                        Persistence persistence = new Persistence(context);
                        persistence.execute(query, "modification");

                        Intent intent = new Intent(context, Verify.class);
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    }
                });
        ((Button)convertView.findViewById(R.id.Denybtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = (String) list.get(position).get(flag[0]);
                        String[] latlong =  ((String) list.get(position).get(flag[2])).split(",");
                        double lat = Double.parseDouble(latlong[0]);
                        double lng = Double.parseDouble(latlong[1]);

                        String query = "delete from Obstacles " +
                                "where afegit_per =\""+username+"\" AND longitud =\""+lng+"\" AND latitud =\""+lat+"\"";

                        Persistence persistence = new Persistence(context);
                        persistence.execute(query, "modification");

                        query =  "update users " +
                                "set obs_falsos = obs_falsos + 1 " +
                                "where username =\""+username+"\"";
                        persistence = new Persistence(context);
                        persistence.execute(query, "modification");


                        Intent intent = new Intent(context, Verify.class);
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    }
                });

        ((Button)convertView.findViewById(R.id.Detailbtn)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = (String) list.get(position).get(flag[0]);
                        String[] latlong =  ((String) list.get(position).get(flag[2])).split(",");
                        double lat = Double.parseDouble(latlong[0]);
                        double lng = Double.parseDouble(latlong[1]);

                        LatLng loc = new LatLng(lat, lng);
                        Geocoder geocoder;
                        List<Address> addresses = new ArrayList<>();
                        geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address addr = addresses.get(0);

                        Toast.makeText(context, "\nAdded by: " + username
                                        + "\nDescription: " + (String) list.get(position).get(flag[1]) +"\nLatitud: " + lat + "\nLongitud: " + lng
                                + "\nPlace: " + addr.getAddressLine(0),
                                Toast.LENGTH_LONG).show();


                    }
                });
        return convertView;
    }
}