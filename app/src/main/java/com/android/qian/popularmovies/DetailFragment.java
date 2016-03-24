package com.android.qian.popularmovies;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private static final String INTENT_EXTRA_MOVIE_DETAIL = "detail";

    private View rootView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(INTENT_EXTRA_MOVIE_DETAIL)) {
            Movie movie = intent.getParcelableExtra(INTENT_EXTRA_MOVIE_DETAIL);
            updateUI(movie);
        }
        return rootView;
    }

    private void updateUI(Movie movie) {
        if (movie == null) { return; }
        ((TextView)rootView.findViewById(R.id.title)).setText(movie.getTitle());
        ((TextView)rootView.findViewById(R.id.releaseDate))
                .setText(movie.getReleaseDate().toString());
        ((TextView)rootView.findViewById(R.id.ratings))
                .setText(Double.toString(movie.getRatings()));
        ((TextView)rootView.findViewById(R.id.overview)).setText(movie.getOverview());
        ImageView imageView = (ImageView)rootView.findViewById(R.id.thumbnail);
        final String IMAGE_BASE_URL =
                "http://image.tmdb.org/t/p/w185/";
        Picasso.with(getContext())
                .load(IMAGE_BASE_URL + movie.getImagePath())
                .into(imageView);
    }
}
