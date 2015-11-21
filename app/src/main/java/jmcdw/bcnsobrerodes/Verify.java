package jmcdw.bcnsobrerodes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.Persistence;

public class Verify extends AppCompatActivity {
    private SimpleAdapter adapter;
    private ListView listView;
    private List<Map<String, Object>> list;
    public static Verify ma;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        list = new ArrayList<Map<String, Object>>();
        listView = new ListView(this);

        try {
            insertObstacles();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MySimpleAdapter adapter = new MySimpleAdapter(this, list,
                R.layout.activity_verify, new String[] { "item1_username",
                "item1_longLat", "item1_description" }, new int[] { R.id.username, R.id.longLat, R.id.description
                 });

        listView.setAdapter(adapter);

        this.setContentView(listView);
    }

    private void insertObstacles() throws ExecutionException, InterruptedException {
        Map<String, Object> map = new HashMap<String, Object>();

        //insertar datos
        map.put("item1_username", "admin");
        map.put("item1_longLat", "411, 422");
        map.put("item1_description", "obstacle blablabla");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("item1_username", "admin123");
        map.put("item1_longLat", "411, 422");
        list.add(map);
    }
}
