package jmcdw.bcnsobrerodes;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Principal extends Activity implements View.OnClickListener {

    private Button places;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        init();
    }


    private void init() {
        places = (Button) findViewById(R.id.button_places);
        places.setOnClickListener(this);

        map = (Button) findViewById(R.id.button_map);
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
}
