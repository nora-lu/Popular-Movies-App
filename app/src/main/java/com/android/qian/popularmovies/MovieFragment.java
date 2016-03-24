package com.android.qian.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final String INTENT_EXTRA_MOVIE_DETAIL = "detail";
    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView)rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(INTENT_EXTRA_MOVIE_DETAIL, mMovieAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences sharedPref
                = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_by_popular));
        Log.e(LOG_TAG, "Sort by: " + sortBy);
        movieTask.execute(sortBy);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {
                // Construct the URL for API query
                // It looks like http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
                // The input parameter supplies the string either "popular" or "top_rated"
                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.e(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();

                Log.e(LOG_TAG, "Movies string: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                mMovieAdapter.clear();
                for (Movie movie : movies) {
                    mMovieAdapter.add(movie);
                }
            }
        }

        private List<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            final String KEY_RESULTS = "results";
            final String KEY_ID = "id";
            final String KEY_OVERVIEW = "overview";
            final String KEY_TITLE = "original_title";
            final String KEY_PATH = "poster_path";
            final String KEY_RATINGS = "vote_average";
            final String KEY_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(KEY_RESULTS);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            int len = moviesArray.length();
            List<Movie> movies = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                int id;
                String title, overview, imagePath;
                double ratings;
                Date releaseDate = new Date();

                JSONObject movieObj = moviesArray.getJSONObject(i);
                id = movieObj.getInt(KEY_ID);
                title = movieObj.getString(KEY_TITLE);
                overview = movieObj.getString(KEY_OVERVIEW);
                imagePath = movieObj.getString(KEY_PATH);
                ratings = movieObj.getDouble(KEY_RATINGS);

                String dateStr = movieObj.getString(KEY_DATE);
                try {
                    releaseDate = format.parse(dateStr);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error parsing date ", e);
                    e.printStackTrace();
                }

                Movie movie = new Movie.MovieBuilder()
                        .id(id)
                        .title(title)
                        .overview(overview)
                        .imagePath(imagePath)
                        .ratings(ratings)
                        .releaseDate(releaseDate)
                        .buildMovie();
                movies.add(movie);
            }
            return movies;
        }
    }
}
