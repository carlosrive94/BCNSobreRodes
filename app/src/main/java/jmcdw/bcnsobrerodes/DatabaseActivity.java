package jmcdw.bcnsobrerodes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import jmcdw.bcnsobrerodes.Utils.Persistence;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView info;
    private Button button, conecta;
    private EditText etQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        etQuery = (EditText) findViewById(R.id.etquery);
        info = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.clear);
        button.setOnClickListener(this);

        conecta = (Button) findViewById(R.id.conecta);
        conecta.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.clear:
                info.setText("dasfdas");
                break;
            case R.id.conecta:
                Persistence persistence = new Persistence(this);
                try {
                    String query = etQuery.getText().toString();
                    String s = persistence.execute(query).get();
                    info.setText(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}


