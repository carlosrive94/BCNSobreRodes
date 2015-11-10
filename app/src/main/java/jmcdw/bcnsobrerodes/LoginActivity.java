package jmcdw.bcnsobrerodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.Persistence;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button admin;
    private Button user;
    private EditText username;
    private EditText password;
    private static Context context;



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
        username = (EditText) findViewById(R.id.editUser);
        password = (EditText) findViewById(R.id.editPass);
        Intent intent;
        switch (arg0.getId()) {
            case R.id.button_user:
              /*  Toast toast = Toast.makeText(this, "User Login not implemented yet", Toast.LENGTH_SHORT);
                toast.show();*/

                intent = new Intent(this, PrincipalActivity.class);
                startActivity(intent);
                break;
            case R.id.button_admin:
                try {
                    int i = userVerification(username.getText().toString(),password.getText().toString());
                   /* Toast toast = Toast.makeText(this, user.getText() + " " + password.getText(), Toast.LENGTH_LONG);
                    toast.show();*/
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //intent = new Intent(this, MapPane.class);
                //startActivity(intent);
                //break;
                //TODO Activity with super user options
        }
    }

    public int userVerification(String user, String password) throws ExecutionException, InterruptedException{
        String query = "select admin,password from users where username=\"" + user + "\"";
        Persistence persistence = new Persistence(this);
        String res = persistence.execute(query, "select").get();
        if (res.equals("")) return -1; //-1 = no existe usuario
        String esAdmin = res.substring(0, 1);
        String psw = res.substring(1);
        if (psw.equals(password)) {
            if (esAdmin.equals("1")) return 1; //1 = login successed and admin;
            else return 2; //2 = login successed but not admin
        }
        Toast toast = Toast.makeText(this, psw + ": " + esAdmin, Toast.LENGTH_LONG);
        toast.show();
        return -2; //incorrect password
    }
}
