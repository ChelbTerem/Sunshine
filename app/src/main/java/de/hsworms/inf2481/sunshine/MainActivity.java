package de.hsworms.inf2481.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.*;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Create some sample data
            List<String> tWeatherData = new ArrayList<String>();
            tWeatherData.add(" 1. Sonntag");
            tWeatherData.add(" 2. Montag");
            tWeatherData.add(" 3. Dienstag");
            tWeatherData.add(" 4. Mittwoch");
            tWeatherData.add(" 5. Donnerstag");
            tWeatherData.add(" 6. Freitag");
            tWeatherData.add(" 7. Samstag");
            tWeatherData.add(" 8. Sonntag");
            tWeatherData.add(" 9. Montag");
            tWeatherData.add("10. Dienstag");
            tWeatherData.add("11. Mittwoch");
            tWeatherData.add("12. Donnerstag");
            tWeatherData.add("13. Freitag");
            tWeatherData.add("14. Samstag");

            // Create the adapter that is responsible for providing the information to the ui
            ArrayAdapter<String> tAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    tWeatherData
            );

            // Allow the adapter to connect between data and ui
            ListView tForecastListView = (ListView) rootView.findViewById(R.id.listview_forecast);
            tForecastListView.setAdapter(tAdapter);

            return rootView;
        }
    }
}
