package jmcdw.bcnsobrerodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import jmcdw.bcnsobrerodes.Utils.Persistence;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView info;
    private Button button, conecta;
    String host = "sql2.freemysqlhosting.net";
    String port = "3306";
    String catalog = "sql294691";
    String user = "sql294691";
    String password = "uX2%mW5!";
    static String query = "select * from LocalitzacioMobilitat";
    private Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        persistence = new Persistence();

        info = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        conecta = (Button) findViewById(R.id.conecta);
        conecta.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.button:
                String resultadoSQL = persistence.executeQuery(false, this, query);
                info.setText(resultadoSQL);
                break;
            case R.id.conecta:
                boolean isConnected = persistence.connectMySQL(this, host, port, catalog, user, password);
                if (isConnected) {
                    Toast.makeText(this,
                            "Connected!",
                            Toast.LENGTH_SHORT).show();
                    conecta.setEnabled(false);
                }
                break;
        }
    }
}


