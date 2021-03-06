package jmcdw.bcnsobrerodes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.MySimpleAdapter;
import jmcdw.bcnsobrerodes.Utils.Persistence;

public class Verify extends AppCompatActivity {
    private SimpleAdapter adapter;
    private ListView listView;
    private List<Map<String, Object>> list;
    private String buttonPressed;
    public static Verify ma;

    private String getOptionPressed(Bundle savedInstanceState){
        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null)return null;
            else return extras.getString("YouClicked");
        }
        else return (String) savedInstanceState.getSerializable("YouClicked");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonPressed = getOptionPressed(savedInstanceState);
        ma = this;
         this.setContentView(R.layout.activity_verify);
        list = new ArrayList<Map<String, Object>>();
        listView = (ListView) findViewById(R.id.listview);
        MySimpleAdapter adapter = new MySimpleAdapter(this, list,
                R.layout.listview, new String[] { "item1_username",
                 "item1_description", "item1_longLat" }, new int[] { R.id.username, R.id.description
        });
        listView.setAdapter(adapter);
        try {
            insertObstacles();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void insertObstacles() throws ExecutionException, InterruptedException {
        String query2 = "SELECT afegit_per, latitud, longitud, descripcio " +
                "FROM Obstacles WHERE verificat = 0 " +
                "order by afegit_per, latitud, longitud";
        Persistence persistence = new Persistence(this);
        String tups = persistence.execute(query2, "select").get();
        if (!tups.equals("")) {
            String tuples[] = tups.split("/");
            for (String tupla : tuples) {
                Log.e("tupla", tupla);
                String[] infoObstacle = tupla.split("-");
                String user = infoObstacle[0];
                double lat = Double.parseDouble(infoObstacle[1]);

                double lng = Double.parseDouble(infoObstacle[2]);

                String desc = infoObstacle[3];

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("item1_username", user);
                map.put("item1_longLat", lat + ", " + lng);
                map.put("item1_description", desc);
                list.add(map);
            }
        }
        else{
            TextView dynamicTextView = new TextView(this);
            dynamicTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            dynamicTextView.setText(" There are no obstacles without ");
            //TODO textview mostrando que no hay nada x validar
        }
    }
}
