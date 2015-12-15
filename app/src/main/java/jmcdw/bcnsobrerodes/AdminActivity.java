package jmcdw.bcnsobrerodes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private Button verify;
    private Button delete;
    private Button ban;
    private int backPressedCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        backPressedCounter = 0;
        init();
    }

    private void init() {
        verify = (Button) findViewById(R.id.verifyButton);
        verify.setOnClickListener(this);

        delete = (Button) findViewById(R.id.deleteButton);
        delete.setOnClickListener(this);

        ban = (Button) findViewById(R.id.banButton);
        ban.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.verifyButton:
                intent = new Intent(this, Verify.class);
                intent.putExtra("YouClicked", "Verify");
                break;
            case R.id.deleteButton:
                intent = new Intent(this, Delete.class);
                intent.putExtra("YouClicked", "Delete");
                break;
            case R.id.banButton:
               // intent.putExtra("YouClicked", "Ban User");
                intent = new Intent(this, BanUser.class);
                intent.putExtra("YouClicked", "BanUser");
                break;
        }
        startActivity(intent);
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
