package jmcdw.bcnsobrerodes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button places;
    private Button map;
    private Button obstacles;
    private SharedPreferences sp;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        printUsername();
        init();
    }

    private void printUsername() {
        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sp.getString("username", null);
        TextView viewUser = (TextView) findViewById(R.id.usernView);
        viewUser.setText(username);
    }


    private void init() {
        places = (Button) findViewById(R.id.button_places);
        places.setOnClickListener(this);

        map = (Button) findViewById(R.id.button_map);
        map.setOnClickListener(this);

        obstacles = (Button) findViewById(R.id.button_obstacles);
        obstacles.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case R.id.button_places:
                intent = new Intent(this, PlacePickerActivity.class);
                startActivity(intent);
                break;
            case R.id.button_map:
                intent = new Intent(this, MapPane.class);
                startActivity(intent);
                break;
            case R.id.button_obstacles:
                intent = new Intent(this, ObstaclesActivity.class);
                startActivity(intent);
                break;
        }
    }
}
