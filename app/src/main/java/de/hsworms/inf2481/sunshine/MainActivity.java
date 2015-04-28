package de.hsworms.inf2481.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

            HttpURLConnection tUrlConnection   = null;
            BufferedReader    tReader          = null;
            String            tForecastJsonStr = null;                 // Will contain the raw JSON response as a string.
            try {
                // Construct the URL for the OpenWeatherMap query
                URL tOpenWeatherURL = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?id=2806142&cnt=7&units=metric&dt=GMT&mode=json");

                // Create the request to OpenWeatherMap, and open the connection
                tUrlConnection = (HttpURLConnection) tOpenWeatherURL.openConnection();
                tUrlConnection.setRequestMethod("GET");
                tUrlConnection.connect();

                // Read the input stream into a String
                InputStream  tInputStream  = tUrlConnection.getInputStream();
                StringBuffer tStringBuffer = new StringBuffer();
                if(tInputStream == null)
                    return null; // Nothing to do

                // Read the stream and add a line break after each line
                tReader = new BufferedReader(new InputStreamReader(tInputStream));
                String tReadLine;
                while((tReadLine = tReader.readLine()) != null)
                    tStringBuffer.append(tReadLine + "\n");

                // Stream was empty.  No point in parsing.
                if(tStringBuffer.length() == 0)
                    return null;

                tForecastJsonStr = tStringBuffer.toString();

            } catch(IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if(tUrlConnection != null)
                    tUrlConnection.disconnect();

                if(tReader != null) {
                    try {
                        tReader.close();
                    } catch(final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }




            return rootView;
        }
    }
}
