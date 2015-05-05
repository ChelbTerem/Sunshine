package de.hsworms.inf2481.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ForecastFragment class
 * @author Chelb
 *
 * This forecast fragment is responsible for the weather data
 * beeing fetched and displayed
 */
public class ForecastFragment extends Fragment {

    private String mWeatherData[];
    private ArrayAdapter<String>    mWeatherDataAdapter;

    /**
     * Default constructor
     */
    public ForecastFragment() {
    }

    @Override
    /**
     * onCreateOptionsMenu
     * Overwritten method that will be called if an options menu will be created.
     * Here you can add custom options for the options menu
     */
    public void onCreateOptionsMenu(Menu xMenu, MenuInflater xInflater) {
        xInflater.inflate(R.menu.forecastfragment, xMenu);
    }


    @Override
    /**
     * onOptionsItemSelected
     * This method will be called if an options item has been selected by the user
     */
    public boolean onOptionsItemSelected(MenuItem xItem) {

        // Check which item has been selected
        int tID = xItem.getItemId();
        if(tID == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        // Unhandled item has been selected
        return super.onOptionsItemSelected(xItem);
    }

    /**
     * updateWeather
     * This method launches the async task to obtain new weather data
     */
    private void updateWeather() {
        SharedPreferences tSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String tZIPCode = tSharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String tUnits   = tSharedPreferences.getString(getString(R.string.pref_unit_key),     getString(R.string.pref_unit_default));

        new FetchWeatherTask().execute(tZIPCode);
    }

    @Override
    /**
     * onStart
     * Overwritten onStart method for this fragment
     */
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    /**
     * onCreate
     * Overwritten onCreate method for this fragment
     */
    public void onCreate(Bundle xSavedInstanceState) {
        super.onCreate(xSavedInstanceState);
        setHasOptionsMenu(true);                // Set this to true to report that this fragment contains an own menu
    }

    @Override
    /**
     * onCreateView
     * This function creates the view for the forecast fragment
     * and returns it
     */
    public View onCreateView(LayoutInflater xInflater, ViewGroup xContainer,
                             Bundle xSavedInstanceState) {

        View tRootView = xInflater.inflate(R.layout.fragment_main, xContainer, false);

        // Create the adapter that is responsible for providing the information to the ui
        mWeatherDataAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>()
        );

        // Allow the adapter to connect between data and ui
        ListView tForecastListView = (ListView) tRootView.findViewById(R.id.listview_forecast);
        tForecastListView.setAdapter(mWeatherDataAdapter);
        tForecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /**
             * onItemClick
             * Defines what happens if the user clicked on an item
             */
            public void onItemClick(AdapterView<?> xParent, View xView, int xPosition, long xID) {

                Intent tShowDetailsIntent = new Intent(getActivity(), DetailActivity.class);
                tShowDetailsIntent.putExtra(Intent.EXTRA_TEXT, mWeatherDataAdapter.getItem(xPosition));
                startActivity(tShowDetailsIntent);
                //Toast.makeText(getActivity(), mWeatherDataAdapter.getItem(xPosition), Toast.LENGTH_LONG).show();
            }
        });
        return tRootView;
    }



    /**
     * FetchWeatherTask class
     * @author Chelb
     *
     * This class fetches the weather-forecast for the next 7 days and
     * returns it to the ui thread by using the AsyncTask functionality
     */
    private class FetchWeatherTask extends AsyncTask<String, Void, String[]>  {

        // Private log tag for this class
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";

        @Override
        /**
         * doInBackground
         * This async task fetches the weather data from openweathermap.org
         */
        protected String[] doInBackground(String... xParams) {

            String tZIPCode;
            String tRequestFormat   = "JSON";
            String tUnits           = "metric";
            int    tNumOfDays       = 7;
            String[] tReturnValue;

            // Interpret the parameters
            if(xParams.length == 0)
                return null;

            // Zip code is the first parameter
            tZIPCode = xParams[0];

            HttpURLConnection tUrlConnection   = null;
            BufferedReader    tReader          = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri tRequest = Uri.parse(OPEN_WEATHER_MAP_URL).buildUpon().appendQueryParameter(QUERY_PARAM, tZIPCode)
                                                                          .appendQueryParameter(FORMAT_PARAM, tRequestFormat)
                                                                          .appendQueryParameter(UNITS_PARAM, tUnits)
                                                                          .appendQueryParameter(DAYS_PARAM, Integer.toString(tNumOfDays))
                                                                          .build();

                // Create the request to OpenWeatherMap, and open the connection
                URL tRequestUrl = new URL(tRequest.toString());
                tUrlConnection = (HttpURLConnection) tRequestUrl.openConnection();
                tUrlConnection.setRequestMethod("GET");
                tUrlConnection.connect();

                // Read the input stream into a String
                InputStream tInputStream  = tUrlConnection.getInputStream();
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

                // Save the returned result
                tReturnValue = getWeatherDataFromJson(tStringBuffer.toString(), tNumOfDays);

            } catch(IOException e) {

                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch(JSONException e) {
                Log.e(LOG_TAG, "JSON Exception ", e);
                return null;
            } finally {

                // Disconnect if the connection has been established
                if(tUrlConnection != null)
                    tUrlConnection.disconnect();

                // Close the reader if needed
                if(tReader != null) {
                    try {
                        tReader.close();
                    } catch(final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return tReturnValue;
        }

        @Override
        /**
         * onPostExecute
         * This function returns the fetched weather data result to the ui thread
         */
        protected void onPostExecute(String[] xStrings) {

            if(xStrings == null)
                return;

            // Remove old data before adding the new one
            mWeatherDataAdapter.setNotifyOnChange(false);
            mWeatherDataAdapter.clear();
            for(String tData :  xStrings)
                mWeatherDataAdapter.add(tData);
            mWeatherDataAdapter.notifyDataSetChanged();
        }



        /** FOLLOWING METHODS HAVE TO BE MOVED TO A SEPARATE CLASS SOMETIME! **/

        private String getReadableDateString(long xTime){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date tDate = new Date(xTime * 1000);
            SimpleDateFormat tFormat = new SimpleDateFormat("E, MMM d");
            return tFormat.format(tDate).toString();
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double xHigh, double xLow) {

            double tHighValue = xHigh;
            double tLowValue  = xLow;

            SharedPreferences tPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String tUnitType = tPreferences.getString(getString(R.string.pref_unit_key), getString(R.string.pref_unit_default));
            if(tUnitType.equals(getString(R.string.pref_unit_imperial))) {
                tHighValue = (tHighValue * 1.8) + 32;
                tLowValue  = (tLowValue  * 1.8) + 32;
            } else if(!tUnitType.equals(getString(R.string.pref_unit_metric))) {
                Log.d(LOG_TAG, "Unknown unit type detected: " + tUnitType);
            }

            // For presentation, assume the user doesn't care about tenths of a degree.
            long tRoundedHigh = Math.round(tHighValue);
            long tRoundedLow  = Math.round(tLowValue);

            String tHighLowStr = tRoundedHigh + "/" + tRoundedLow;
            return tHighLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String xForecastJsonStr, int xNumDays) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST           = "list";
            final String OWM_WEATHER        = "weather";
            final String OWM_TEMPERATURE    = "temp";
            final String OWM_MAX            = "max";
            final String OWM_MIN            = "min";
            final String OWM_DATETIME       = "dt";
            final String OWM_DESCRIPTION    = "main";

            // Start parsing the JSON data
            JSONObject tJSONWeatherData = new JSONObject(xForecastJsonStr);
            JSONArray tWeatherArray    = tJSONWeatherData.getJSONArray(OWM_LIST);

            // Create an array that contains the weather data for every day
            String[] tResultStrings = new String[xNumDays];
            for(int a = 0; a < tWeatherArray.length(); a++) {
                // For now, using the format "Day, description, hi/low"
                String tDay;
                String tDescription;
                String tHighAndLow;

                // Get the JSON object representing the day
                JSONObject tDayForecast = tWeatherArray.getJSONObject(a);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long tDateTime = tDayForecast.getLong(OWM_DATETIME);
                tDay = getReadableDateString(tDateTime);

                // Description is in a child array called "weather", which is 1 element long.
                JSONObject tWeatherObject = tDayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                tDescription              = tWeatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject tTemperatureObject = tDayForecast.getJSONObject(OWM_TEMPERATURE);
                double tHigh = tTemperatureObject.getDouble(OWM_MAX);
                double tLow  = tTemperatureObject.getDouble(OWM_MIN);

                tHighAndLow = formatHighLows(tHigh, tLow);
                tResultStrings[a] = tDay + " - " + tDescription + " - " + tHighAndLow;
            }

            return tResultStrings;
        }
    }
}
