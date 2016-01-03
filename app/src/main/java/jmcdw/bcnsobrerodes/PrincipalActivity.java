package jmcdw.bcnsobrerodes;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PrincipalActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton places;
    private ImageButton map;
    private SharedPreferences sp;
    private String username;
    private int backPressedCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backPressedCounter = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        printUsername();
        init();
    }

    private void printUsername() {
        sp = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sp.getString("username", null);
        TextView viewUser = (TextView) findViewById(R.id.usernView);
        viewUser.setText("Logged as " + username);
    }


    private void init() {
        places = (ImageButton) findViewById(R.id.button_places);
        places.setOnClickListener(this);

        map = (ImageButton) findViewById(R.id.button_map);
        map.setOnClickListener(this);
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
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedCounter != 0) super.onBackPressed();
        else {
            ++backPressedCounter;
            Toast.makeText(this, "Press back again to logout",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
