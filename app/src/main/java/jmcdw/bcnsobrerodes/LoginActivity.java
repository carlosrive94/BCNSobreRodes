package jmcdw.bcnsobrerodes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button admin;
    private Button user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }


    private void init() {
        user = (Button) findViewById(R.id.button_user);
        user.setOnClickListener(this);

        admin = (Button) findViewById(R.id.button_admin);
        admin.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        Intent intent;
        switch (arg0.getId()) {
            case R.id.button_user:
                Toast toast = Toast.makeText(this, "User Login not implemented yet", Toast.LENGTH_SHORT);
                toast.show();
                intent = new Intent(this, PrincipalActivity.class);
                startActivity(intent);
                break;
            case R.id.button_admin:
                //intent = new Intent(this, MapPane.class);
                //startActivity(intent);
                //break;
                //TODO Activity with super user options
        }
    }
}
