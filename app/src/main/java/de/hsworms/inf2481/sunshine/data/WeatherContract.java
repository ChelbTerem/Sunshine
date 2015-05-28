package de.hsworms.inf2481.sunshine.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;
import android.content.ContentResolver;
import android.content.ContentUris;

/**
 * Created by Chelb on 17.05.2015.
 */
public class WeatherContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "de.hsworms.inf2481.sunshine";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WEATHER  = "weather";
    public static final String PATH_LOCATION = "location";

    //

    /**
     * normalizeDate
     * To make it easy to query for the exact date, we normalize all dates that go into
     * the database to the start of the the Julian day at UTC.
     * @param xStartDate
     * @return normalized date
     */
    public static long normalizeDate(long xStartDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(xStartDate);
        int julianDay = Time.getJulianDay(xStartDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /**
     * LocationEntry class
     * Inner class that defines the contents of the location table
     */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri    CONTENT_URI          = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        // The location setting string is what will be sent to openweathermap
        // as the location query.
        public static final String COLUMN_LOCATION_SETTING = "location_setting";

        // Human readable location string, provided by the API.  Because for styling,
        // "Mountain View" is more recognizable than 94043.
        public static final String COLUMN_CITY_NAME = "city_name";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by openweathermap.
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_long";

        /**
         * buildLocationUri
         * @param xID
         * @return
         */
        public static Uri buildLocationUri(long xID) {
            return ContentUris.withAppendedId(CONTENT_URI, xID);
        }
    }

    /* Inner class that defines the contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI             = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE         = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE    = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long xID) {
            return ContentUris.withAppendedId(CONTENT_URI, xID);
        }

        /**
         * buildWeatherLocation
         * @param xLocationSetting
         * @return
         */
        public static Uri buildWeatherLocation(String xLocationSetting) {
            return CONTENT_URI.buildUpon().appendPath(xLocationSetting).build();
        }

        /**
         * buildWeatherLocationWithStartDate
         * @param xLocationSetting
         * @param xStartDate
         * @return
         */
        public static Uri buildWeatherLocationWithStartDate(String xLocationSetting, long xStartDate) {
            long tNormalizedDate = normalizeDate(xStartDate);
            return CONTENT_URI.buildUpon().appendPath(xLocationSetting).appendQueryParameter(COLUMN_DATE, Long.toString(tNormalizedDate)).build();
        }

        /**
         * buildWeatherLocationWithDate
         * @param xLocationSetting
         * @param xDate
         * @return
         */
        public static Uri buildWeatherLocationWithDate(String xLocationSetting, long xDate) {
            return CONTENT_URI.buildUpon().appendPath(xLocationSetting).appendPath(Long.toString(normalizeDate(xDate))).build();
        }

        /**
         * getLocationSettingFromUri
         * @param xUri
         * @return
         */
        public static String getLocationSettingFromUri(Uri xUri) {
            return xUri.getPathSegments().get(1);
        }

        /**
         * getDateFromUri
         * @param xUri
         * @return
         */
        public static long getDateFromUri(Uri xUri) {
            return Long.parseLong(xUri.getPathSegments().get(2));
        }

        /**
         * getStartDateFromUri
         * @param xUri
         * @return
         */
        public static long getStartDateFromUri(Uri xUri) {
            String tDateString = xUri.getQueryParameter(COLUMN_DATE);
            if(tDateString != null && tDateString.length() > 0)
                return Long.parseLong(tDateString);
            return 0;
        }
    }

}
