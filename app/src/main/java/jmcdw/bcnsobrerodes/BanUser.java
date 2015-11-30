package jmcdw.bcnsobrerodes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.MySimpleAdapter2;
import jmcdw.bcnsobrerodes.Utils.MySimpleAdapter3;
import jmcdw.bcnsobrerodes.Utils.Persistence;

public class BanUser extends AppCompatActivity {

    private SimpleAdapter adapter;
    private ListView listView;
    private List<Map<String, Object>> list;
    private String buttonPressed;
    public static BanUser ma;

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
        this.setContentView(R.layout.activity_ban_user);
        list = new ArrayList<Map<String, Object>>();
        listView = (ListView) findViewById(R.id.listview3);
        Intent intent = getIntent();
        MySimpleAdapter3 adapter = new MySimpleAdapter3(this, list,
                R.layout.listview3, new String[] { "item1_username",
                "item1_obsfalsos", "item1_baneado" }, new int[] { R.id.username3, R.id.obsfalsos, R.id.baneado
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
        String query2 = "SELECT username, obs_falsos, esta_baneado " +
                "FROM users " +
                "where admin = 0 "+
                "order by username";
        Persistence persistence = new Persistence(this);
        String tups = persistence.execute(query2, "select").get();
        if (!tups.equals("")) {
            String tuples[] = tups.split("/");
            for (String tupla : tuples) {
                Log.e("tupla", tupla);
                String[] infoObstacle = tupla.split("-");
                String user = infoObstacle[0];
                String obsfalsos = infoObstacle[1];
                int baneado = Integer.parseInt(infoObstacle[2]);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("item1_username", user);
                map.put("item1_obsfalsos", obsfalsos);
                map.put("item1_baneado", (baneado==0)?"false":"true");
                list.add(map);
            }
        }
    }
}
