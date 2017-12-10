package com.example.doubanmovietop250.item;

public class MovieItem {
    private int rank;
    private String title;
    private String imageUrl;
    private float rating;
    private String id;
    private String genres;
    private String year;

    public MovieItem(int rank, String title, String imageUrl, float rating,
                     String id, String genres, String year) {
        this.rank = rank;
        this.title = title;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.id = id;
        this.genres = genres;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getRank() {
        return rank;
    }

    public float getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }

    public String getGenres() {
        return genres;
    }

    public String getYear() {
        return year;
    }
}
