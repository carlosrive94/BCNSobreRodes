package jmcdw.bcnsobrerodes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.Persistence;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button admin;
    private Button user;
    private Button newAccount;
    private EditText username;
    private EditText password;



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

        newAccount = (Button) findViewById(R.id.button_create);
        newAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        username = (EditText) findViewById(R.id.editUser);
        password = (EditText) findViewById(R.id.editPass);
        Intent intent;
        switch (arg0.getId()) {
            case  R.id.button_create:
                newAccount();
                break;

            case R.id.button_user:
                intent = new Intent(this, PrincipalActivity.class);
                startActivity(intent);
                break;

            case R.id.button_admin:
                try {
                    Toast loginResult = new Toast(this);
                    if (!(username.getText().toString().equals("") || password.getText().toString().equals(""))) {
                        int i = userVerification(username.getText().toString(), password.getText().toString());
                        switch (i) {
                            case -1:
                                loginResult = Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT);
                                break;
                            case -2:
                                loginResult = Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT);
                                break;
                            case 1:
                                loginResult = Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT);
                                break;
                            case 2:
                                loginResult = Toast.makeText(this, "This user is not an Admin", Toast.LENGTH_SHORT);
                                break;
                        }

                    }

                    else loginResult = Toast.makeText(this, "Please, fill the form", Toast.LENGTH_SHORT);
                    loginResult.show();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TODO Activity with super user options
        }
    }

    public void newAccount (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.new_account_dialog,null))
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText usText = (EditText) ((AlertDialog) dialog).findViewById(R.id.userText);
                        EditText passText = (EditText) ((AlertDialog) dialog).findViewById(R.id.PassText);
                        EditText verifPassText = (EditText) ((AlertDialog) dialog).findViewById(R.id.PassText2);
                        EditText email = (EditText) ((AlertDialog) dialog).findViewById(R.id.mailText);

                        String usuario = usText.getText().toString();
                        String password = passText.getText().toString();
                        String verif = verifPassText.getText().toString();
                        String mail = email.getText().toString();

                        Toast t;

                        if (usuario.equals("") || password.equals("") || verif.equals("") || mail.equals("")) {
                            t = Toast.makeText(LoginActivity.this, "Please fill all the blankets", Toast.LENGTH_SHORT);
                            t.show();
                        } else if (!password.equals(verif)) {
                            t = Toast.makeText(LoginActivity.this, "Passwords don't match", Toast.LENGTH_SHORT);
                            t.show();
                        }

                        else try {

                            int success = checkUser(usuario, password, mail);
                            if (success == 0) addUser(usuario, password, mail);

                            switch (success) {
                                case 0:
                                    t = Toast.makeText(LoginActivity.this, "User " + usuario + " created", Toast.LENGTH_SHORT);
                                    t.show();
                                    break;
                                case -1:
                                    t = Toast.makeText(LoginActivity.this, "User " + usuario + " already exists", Toast.LENGTH_SHORT);
                                    t.show();
                                    break;
                                case -2:
                                    t = Toast.makeText(LoginActivity.this, "There is already an account with this email", Toast.LENGTH_SHORT);
                                    t.show(); break;

                            }

                        } catch (Exception exception) {
                            t = Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
                            t.show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public int checkUser(String user, String password, String email) throws Exception{
        Persistence persistence = new Persistence(this);
        String q1 = "select username, email from users where username=\"" + user + "\" or email=\"" + email +"\"";
        String row[] = persistence.execute(q1, "select").get().split("-");
        //persistence.cancel(false);

        if (row[0].equals(user)) return -1;
        else if (row[1].equals(email)) return -2;
        else return 0;
    }

    public void addUser(String user, String password, String email) throws Exception{
        String query = "insert into users(username, email,password)" +
                " values(\"" + user + "\", \"" + email + "\", \"" + password + "\")";
        Persistence persistence = new Persistence(this);
        persistence.execute(query, "modification");
    }


    public int userVerification(String user, String password) throws ExecutionException, InterruptedException{
        String query = "select admin,password from users where username=\"" + user + "\"";
        Persistence persistence = new Persistence(this);
        String res[] = persistence.execute(query, "select").get().split("-");
        if (res[0].equals("")) return -1; //-1 = no existe usuario

        if (res[1].equals(password)) {
            if (res[0].equals("1")) return 1; //1 = login successed and admin;
            else return 2; //2 = login successed but not admin
        }
        return -2; //incorrect password
    }
}
