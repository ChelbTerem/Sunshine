package de.hsworms.inf2481.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


/**
 * DetailActivityFragment class
 * @author Chelb
 *
 * This class represents the fragment of the detail activity
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String SHARE_HASHTAG = "#Invalid";
    private String mForecastString;

    /**
     * Default constructor
     */
    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    /**
     * onCreateView
     * Overwritten onCreateView method for this fragment
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View tView = inflater.inflate(R.layout.fragment_detail, container, false);

        /// Obtain the provided extras from the launch intent
        Bundle tExtras = getActivity().getIntent().getExtras();
        if(tExtras != null)
            mForecastString = tExtras.getString(Intent.EXTRA_TEXT);

        // Pass the information to the text view
        TextView tTextView = (TextView) tView.findViewById(R.id.detail_text_view);
        tTextView.setText(mForecastString);

        return tView;
    }

    @Override
    /**
     * onCreateOptionsMenu
     * This method creates the custom options menu for this fragment
     */
    public void onCreateOptionsMenu(Menu xMenu, MenuInflater xInflater) {

        xInflater.inflate(R.menu.detailfragment, xMenu);

        MenuItem tItem = xMenu.findItem(R.id.action_share);
        ShareActionProvider tActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(tItem);

        if(tActionProvider != null)
            tActionProvider.setShareIntent(createShareIntent());
        else
            Log.d(LOG_TAG, "Share action provider is null");
    }

    /**
     * createShareIntent
     * This method creates and returns the share intent for the selected weather details
     * @return Intent that will share the content
     */
    private Intent createShareIntent() {
        Intent tShareIntent = new Intent(Intent.ACTION_SEND);
        tShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        tShareIntent.setType("text/plain");
        tShareIntent.putExtra(Intent.EXTRA_TEXT, mForecastString + SHARE_HASHTAG);
        return tShareIntent;
    }
}
