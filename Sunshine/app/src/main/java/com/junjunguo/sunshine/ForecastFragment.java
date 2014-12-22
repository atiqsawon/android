package com.junjunguo.sunshine;

/**
 * Created by GuoJunjun on 20.12.14.
 */

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view. 1<p/> a fragment is a modular container with
 * activity
 * <p/>
 * fragment_main: res/layout/fragment_main.xml
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("Trondheim");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        List<String> alist = new ArrayList<>();
        alist.add("Today - Sunny - 88/63");
        alist.add("Tomorrow - Sunny - 88/63");
        alist.add("Weds - Sunny - 88/63");
        alist.add("Thurs - Sunny - 88/63");
        alist.add("Fri - Sunny - 88/63");
        alist.add("Sat - Sunny - 88/63");
        //        alist.add(FetchWeatherTask01.getWeather("Trondheim"));
            /*
             *  ArrayAdapter<String> :
             *  Parameters:
             *      Context:
             *              contained global information about app environment*
             *      ID of list item layout
             *              *
             *      ID of text view
             *      list of data
             */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                // the current context
                getActivity(),
                // id of list item layout
                R.layout.list_item_forecast,
                // id of the textview to populate
                R.id.list_item_forecast_textview,
                // data
                alist);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // get a reference to the ListView, and attach this adapter to listview
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(arrayAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //                URL url = new URL("http://api.openweathermap.org/data/2
                // .5/forecast/daily?q=" + "Trondheim" + "&mode=json&units=metric&cnt=7");
                final String forecastBaseUrl =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String queryParam = "q";
                final String formatParam = "mode";
                final String unitsParam = "units";
                final String daysParam = "cnt";
                Uri builtUri = Uri.parse(forecastBaseUrl).buildUpon()
                        .appendQueryParameter(queryParam, params[0])
                        .appendQueryParameter(formatParam, format)
                        .appendQueryParameter(unitsParam, units)
                        .appendQueryParameter(daysParam, Integer.toString(numDays)).build();
                // Create the request to OpenWeatherMap, and open the connection
                //                urlConnection = (HttpURLConnection) url.openConnection();
                //                urlConnection.setRequestMethod("GET");
                //                urlConnection.connect();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    //                forecastJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    //                forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, 
                // there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
