package com.example.doubanmovietop250.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doubanmovietop250.R;
import com.example.doubanmovietop250.adapter.StaffAdapter;
import com.example.doubanmovietop250.gson.MovieInfoBean;
import com.example.doubanmovietop250.item.StaffItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieInfoActivity extends AppCompatActivity {

    private ImageView movieImageView;

    private TextView movieTitle;

    private TextView movieContentText;

    private String id;

    private String shareUrl;

    private List<StaffItem> staffList = new ArrayList<>();

    private RecyclerView recyclerView;

    private StaffAdapter adapter;

    private TextView movieRating;

    private TextView movieGenres;

    private TextView movieYear;

    private TextView movieCountries;

    private TextView movieRatingsCount;

    private String mobileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        Intent intent = getIntent();
        id = intent.getStringExtra("movie_id");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        movieImageView = (ImageView) findViewById(R.id.movie_image_view);
        movieTitle = (TextView) findViewById(R.id.movie_title);
        movieContentText = (TextView) findViewById(R.id.movie_content_text);
        movieRating = (TextView) findViewById(R.id.movie_rating);
        movieGenres = (TextView) findViewById(R.id.movie_genres);
        movieYear = (TextView) findViewById(R.id.movie_year);
        movieCountries = (TextView) findViewById(R.id.movie_countries);
        movieRatingsCount = (TextView) findViewById(R.id.movie_ratings_count);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StaffAdapter(staffList);
        recyclerView.setAdapter(adapter);
        requestMovieInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_info_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                Intent intentShare = new Intent();
                intentShare.setAction(Intent.ACTION_SEND);
                intentShare.putExtra(Intent.EXTRA_TEXT, shareUrl);
                intentShare.setType("text/plain");
                startActivity(Intent.createChooser(intentShare, getString(R.string.share_title)));
                return true;
            case R.id.open_in_browser:
                Intent intentOpenBrowser = new Intent(Intent.ACTION_VIEW);
                intentOpenBrowser.setData(Uri.parse(mobileUrl));
                startActivity(intentOpenBrowser);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestMovieInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://api.douban.com/v2/movie/subject/" + id)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    MovieInfoBean movieInfoBean = gson.fromJson(responseData, MovieInfoBean.class);
                    final String imageUrl = movieInfoBean.getImagesBean().getLarge();
                    final String summary = movieInfoBean.getSummary();
                    final String title = movieInfoBean.getTitle();
                    final float rating = movieInfoBean.getRating().getAverage();
                    shareUrl = movieInfoBean.getShareUrl();
                    mobileUrl = movieInfoBean.getMobileUrl();
                    int directorsLength = movieInfoBean.getDirectors().size();
                    for (int i = 0; i < directorsLength; i++) {
                        String staffName = movieInfoBean.getDirectors().get(i).getName();
                        String staffImageUrl = movieInfoBean.getDirectors().get(i)
                                .getAvatars().getSmall();
                        String staffId = movieInfoBean.getDirectors().get(i).getId();
                        staffList.add(new StaffItem(staffName, staffImageUrl, staffId));
                    }
                    int castsLength = movieInfoBean.getCasts().size();
                    for (int i = 0; i < castsLength; i++) {
                        String staffName = movieInfoBean.getCasts().get(i).getName();
                        String staffImageUrl = movieInfoBean.getCasts().get(i)
                                .getAvatars().getSmall();
                        String staffId = movieInfoBean.getCasts().get(i).getId();
                        staffList.add(new StaffItem(staffName, staffImageUrl, staffId));
                    }
                    int genreLength = movieInfoBean.getGenres().size();
                    StringBuilder genreBuilder = new StringBuilder();
                    for (int j = 0; j < genreLength - 1; j++) {
                        genreBuilder.append(movieInfoBean.getGenres().get(j));
                        genreBuilder.append(",");
                    }
                    genreBuilder.append(movieInfoBean.getGenres().get(genreLength - 1));
                    final String genres = genreBuilder.toString();
                    final String year = movieInfoBean.getYear();
                    int countryLength = movieInfoBean.getCountries().size();
                    StringBuilder countryBuilder = new StringBuilder();
                    for (int j = 0; j < countryLength - 1; j++) {
                        countryBuilder.append(movieInfoBean.getCountries().get(j));
                        countryBuilder.append(",");
                    }
                    countryBuilder.append(movieInfoBean.getCountries().get(countryLength - 1));
                    final String countries = countryBuilder.toString();
                    final int ratingsCount = movieInfoBean.getRatingsCount();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(MovieInfoActivity.this).load(imageUrl)
                                    .into(movieImageView);
                            movieTitle.setText(title);
                            movieContentText.setText(summary);
                            movieRating.setText(String.valueOf(rating));
                            movieGenres.setText(genres);
                            movieYear.setText(year);
                            movieCountries.setText(countries);
                            movieRatingsCount.setText(String.valueOf(ratingsCount) + "人评分");
                            LinearLayout layout = (LinearLayout) findViewById(
                                    R.id.scroll_view_child_layout);
                            adapter.notifyDataSetChanged();
                            layout.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, MovieInfoActivity.class);
        intent.putExtra("movie_id", id);
        context.startActivity(intent);
    }
}
