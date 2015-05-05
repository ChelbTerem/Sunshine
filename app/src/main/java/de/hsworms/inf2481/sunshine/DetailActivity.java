package de.hsworms.inf2481.sunshine;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * DetailActivity class
 * @author Chelb
 *
 * This class shows the details for the selected forecast entry.
 * The needed data is provided via the launch extend as an EXTRA_TEXT
 */
public class DetailActivity extends ActionBarActivity {

    @Override
    /**
     * onCreate
     * Overwritten onCreate method. This method will be called if the activity gets created
     */
    protected void onCreate(Bundle xSavedInstanceState) {
        super.onCreate(xSavedInstanceState);
        setContentView(R.layout.activity_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    /**
     * onOptionsItemSelected
     * This method handles item selections from the options menu
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        // Check what item has been selected
        int tItemID = item.getItemId();
        if(tItemID == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
