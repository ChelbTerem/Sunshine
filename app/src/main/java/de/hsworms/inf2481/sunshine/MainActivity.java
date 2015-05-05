package de.hsworms.inf2481.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        } else if(tItemID == R.id.action_map) {
            openPreferredLocationOnMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * openPreferredLocationOnMap
     * This helper method opens the preferred location of the user by launching
     * an implicite intent
     */
    private void openPreferredLocationOnMap() {
        SharedPreferences tPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tLocation = tPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        // Build the geo location for the location
        Uri tGeoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", tLocation)
                .build();

        // Create and launch the intent
        Intent tLaunchIntent = new Intent(Intent.ACTION_VIEW);
        tLaunchIntent.setData(tGeoLocation);
        if(tLaunchIntent.resolveActivity(getPackageManager()) != null)
            startActivity(tLaunchIntent);
        else
            Log.d(LOG_TAG, "Could not show " + tLocation + " because no valid application found to handle this call.");
    }
}
