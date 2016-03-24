package com.android.qian.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Qian on 3/19/2016.
 */
public class Movie implements Parcelable{
    private int id;
    private String title;
    private String imagePath;  // the relative path to a movie poster image
    private String overview;
    private double ratings;
    private Date releaseDate;

    private Movie(int id, String title, String imagePath, String overview,
                 double ratings, Date releaseDate) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.overview = overview;
        this.ratings = ratings;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        imagePath = in.readString();
        overview = in.readString();
        ratings = in.readDouble();
        releaseDate = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(overview);
        dest.writeDouble(ratings);
        dest.writeLong(releaseDate.getTime());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getOverview() {
        return overview;
    }

    public double getRatings() {
        return ratings;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Uses the Builder Design Pattern
     */
    public static class MovieBuilder {
        private int nestedId;
        private String nestedTitle;
        private String nestedImagePath;
        private String nestedOverview;
        private double nestedRatings;
        private Date nestedReleaseDate;

        public MovieBuilder(){}

        public Movie buildMovie() {
            return new Movie(nestedId, nestedTitle, nestedImagePath,
                    nestedOverview, nestedRatings, nestedReleaseDate);
        }

        public MovieBuilder id(int id) {
            this.nestedId = id;
            return this;
        }

        public MovieBuilder title(String title) {
            this.nestedTitle = title;
            return this;
        }

        public MovieBuilder imagePath(String path) {
            this.nestedImagePath = path;
            return this;
        }

        public MovieBuilder overview(String overview) {
            this.nestedOverview = overview;
            return this;
        }

        public MovieBuilder ratings(double ratings) {
            this.nestedRatings = ratings;
            return this;
        }

        public MovieBuilder releaseDate(Date date) {
            this.nestedReleaseDate = date;
            return this;
        }
    }
}
