package com.salvatop.android.moviedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.salvatop.android.moviedb.utilities.NetworkUtils;
import com.salvatop.android.moviedb.utilities.JsonUtils;


import java.net.URL;

import static com.salvatop.android.moviedb.utilities.NetworkUtils.getResponse;


public class MainActivity extends AppCompatActivity implements MovieAdapter.adapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView movieGrid;
    private MovieAdapter movieAdapter;
    private TextView error;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieGrid = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        error = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(
                this, GridLayoutManager.chooseSize(2,2,2));

        movieGrid.setLayoutManager(layoutManager);
        movieGrid.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);

        movieGrid.setAdapter(movieAdapter);

        loading = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovies("discover");
    }

    /**
     * This method will get the user's preferred sorting and
     * get the data in the background.
     */
    private void loadMovies(String sortBy) {
        showMoviesView();
        new FetchTaskToExecute().execute(sortBy);
    }

    @Override
    public void onClick(String movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }

    private void showMoviesView() {
        error.setVisibility(View.INVISIBLE);
        movieGrid.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        movieGrid.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
    }

    public class FetchTaskToExecute extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            /* discover movies by default */

            String sortBy = params[0];
            URL requestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonResponse = getResponse(requestUrl);
                String[] movies = JsonUtils.getMovieJson(MainActivity.this, jsonResponse);

                return movies;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movie) {
            loading.setVisibility(View.INVISIBLE);
            if (movie != null) {
                showMoviesView();
                movieAdapter.setMovie(movie);
            } else {
                showErrorMessage();
            }
        }
    }

    private void sortByRating() {
        getSupportActionBar().setTitle(R.string.sorted_by_rating_name);
        loadMovies("rating");
    }

    private void sortByRPopularity() {
        getSupportActionBar().setTitle(R.string.sorted_by_popularity_name);
        loadMovies("popularity");
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
            movieAdapter.setMovie(null);
            loadMovies("discover");
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