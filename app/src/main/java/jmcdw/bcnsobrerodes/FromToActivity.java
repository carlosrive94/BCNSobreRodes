package jmcdw.bcnsobrerodes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class FromToActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_from_to);
    }

    public void onSearchRoute(View view) {
        Intent returnIntent = new Intent();
        EditText source = (EditText)findViewById(R.id.FromText);
        EditText dest = (EditText)findViewById(R.id.ToText);
        Editable from = source.getText();
        Editable to = dest.getText();
        returnIntent.putExtra("from", from.toString());
        returnIntent.putExtra("to", to.toString());
        // Activity finished ok, return the data
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
