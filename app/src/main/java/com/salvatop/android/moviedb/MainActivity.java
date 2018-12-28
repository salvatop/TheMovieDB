
package com.salvatop.android.moviedb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.salvatop.android.moviedb.utilities.NetworkUtils;
import com.salvatop.android.moviedb.utilities.JsonUtils;


import java.net.URL;

import static com.salvatop.android.moviedb.utilities.NetworkUtils.getResponseFromHttpUrl;


public class MainActivity extends AppCompatActivity implements MovieAdapter.adapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, GridLayoutManager.chooseSize(2,2,2));

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        mMovieAdapter = new MovieAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        loadWeatherData();
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadWeatherData() {
        showWeatherDataView();
        new FetchWeatherTask().execute("");
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param weatherForDay The weather for the day that was clicked
     */
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intentToStartDetailActivity);
    }

    private void showWeatherDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = JsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                showWeatherDataView();
                mMovieAdapter.setMovie(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

    private void sortByRating() {
        getSupportActionBar().setTitle(R.string.sorted_by_rating_name);
    }

    private void sortByRPopularity() {
        getSupportActionBar().setTitle(R.string.sorted_by_popularity_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mMovieAdapter.setMovie(null);
            loadWeatherData();
            return true;
        }

        if (id == R.id.actionSortByPopularity) {
            sortByRPopularity();
            return true;
        }

        if (id == R.id.actionSortByRating) {
            sortByRating();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}