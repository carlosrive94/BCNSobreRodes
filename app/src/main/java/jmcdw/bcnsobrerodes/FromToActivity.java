package jmcdw.bcnsobrerodes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FromToActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_to);
    }

    public void onSearchRoute(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("from", R.id.FromText);
        returnIntent.putExtra("to", R.id.ToText);
        // Activity finished ok, return the data
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
