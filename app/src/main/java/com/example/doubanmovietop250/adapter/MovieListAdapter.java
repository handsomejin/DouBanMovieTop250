package com.example.doubanmovietop250.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doubanmovietop250.activity.MovieInfoActivity;
import com.example.doubanmovietop250.item.MovieItem;
import com.example.doubanmovietop250.R;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {
    private Context mContext;
    private List<MovieItem> movieItemList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView movieRank;
        ImageView movieImage;
        TextView movieTitle;
        RatingBar movieRating;
        TextView movieRatingText;
        TextView movieGenres;
        TextView movieYear;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            movieRank = (TextView) view.findViewById(R.id.movie_rank);
            movieImage = (ImageView) view.findViewById(R.id.movie_image);
            movieTitle = (TextView) view.findViewById(R.id.movie_title);
            movieRating = (RatingBar) view.findViewById(R.id.movie_rating);
            movieRatingText = (TextView) view.findViewById(R.id.movie_rating_text);
            movieGenres = (TextView) view.findViewById(R.id.movie_genres);
            movieYear = (TextView) view.findViewById(R.id.movie_year);
        }
    }

    public MovieListAdapter(List<MovieItem> movieItemList) {
        this.movieItemList = movieItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item,
                parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String id = movieItemList.get(position).getId();
                MovieInfoActivity.actionStart(mContext, id);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieItem movieItem = movieItemList.get(position);
        holder.movieRank.setText(String.valueOf(movieItem.getRank()));
        Glide.with(mContext).load(movieItem.getImageUrl()).into(holder.movieImage);
        holder.movieTitle.setText(movieItem.getTitle());
        holder.movieRating.setRating(generateRating(movieItem.getRating()));
        holder.movieRatingText.setText(String.valueOf(movieItem.getRating()));
        holder.movieGenres.setText(movieItem.getGenres());
        holder.movieYear.setText(movieItem.getYear());
    }

    @Override
    public int getItemCount() {
        return movieItemList.size();
    }

    // 分段返回不同的值，用于RatingBar
    public float generateRating(float rating) {
        if (rating > ((float) 9.0)) {
            return ((float) 5.0);
        } else if (rating > ((float) 8.0)) {
            return ((float) 4.5);
        } else if (rating > ((float) 7.0)) {
            return ((float) 4.0);
        } else if (rating > ((float) 6.0)) {
            return ((float) 3.5);
        } else if (rating > ((float) 5.0)) {
            return ((float) 3.0);
        } else if (rating > ((float) 4.0)) {
            return ((float) 2.5);
        } else if (rating > ((float) 3.0)) {
            return ((float) 2.0);
        } else if (rating > ((float) 2.0)) {
            return ((float) 1.5);
        } else if (rating > ((float) 1.0)) {
            return ((float) 1.0);
        } else if (rating > ((float) 0.0)) {
            return ((float) 0.5);
        } else {
            return ((float) 0.0);
        }
    }
}
