package com.example.doubanmovietop250.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.doubanmovietop250.R;
import com.example.doubanmovietop250.adapter.MovieListAdapter;
import com.example.doubanmovietop250.gson.MovieListBean;
import com.example.doubanmovietop250.item.MovieItem;
import com.example.doubanmovietop250.listener.EndlessRecyclerOnScrollListener;
import com.example.doubanmovietop250.wrapper.LoadMoreWrapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieListActivity extends AppCompatActivity {

    private List<MovieItem> movieList = new ArrayList<>();

    private List<MovieItem> poiMovieList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;

    private LoadMoreWrapper loadMoreWrapper;

    private RecyclerView recyclerView;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MovieListAdapter adapter = new MovieListAdapter(movieList);
        loadMoreWrapper = new LoadMoreWrapper(adapter);
        recyclerView.setAdapter(loadMoreWrapper);
        requestMovieList();
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMovieItem();
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (!isLoading) {
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);
                    isLoading = true;
                    if (movieList.size() <= 225) {
                        loadMovieItem();
                    } else {
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                        isLoading = false;
                    }
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void requestMovieList() {
        // Android在主线程不能执行http请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    movieList.clear();
                    sendRequestAndParse(movieList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreWrapper.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void refreshMovieItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    poiMovieList.clear();
                    sendRequestAndParse(poiMovieList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int previousSize = movieList.size();
                            movieList.clear();
                            loadMoreWrapper.notifyItemRangeRemoved(0, previousSize);
                            movieList.addAll(poiMovieList);
                            loadMoreWrapper.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // 递归执行刷新函数
                    refreshMovieItem();
                }
            }
        }).start();
    }

    private void loadMovieItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRequestAndParse(movieList);
                    isLoading = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // 递归执行加载函数
                    loadMovieItem();
                }
            }
        }).start();
    }

    private void sendRequestAndParse(List<MovieItem> list) throws Exception {
        int listSize = list.size();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.douban.com/v2/movie/top250?start=" + listSize + "&count=25")
                .build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        Gson gson = new Gson();
        MovieListBean movieListBean = gson.fromJson(responseData, MovieListBean.class);
        for (int i = 0; i < 25; i++) {
            String title = movieListBean.getSubjects().get(i).getTitle();
            String imageUrl = movieListBean.getSubjects().get(i).getImages().getSmall();
            float rating = movieListBean.getSubjects().get(i).getRating().getAverage();
            String id = movieListBean.getSubjects().get(i).getId();
            int genreLength = movieListBean.getSubjects().get(i).getGenres().size();
            StringBuilder genreBuilder = new StringBuilder();
            for (int j = 0; j < genreLength - 1; j++) {
                genreBuilder.append(movieListBean.getSubjects().get(i).getGenres().get(j));
                genreBuilder.append(",");
            }
            genreBuilder.append(movieListBean.getSubjects().get(i).getGenres()
                    .get(genreLength - 1));
            String genres = genreBuilder.toString();
            String year = movieListBean.getSubjects().get(i).getYear();
            list.add(new MovieItem(
                    listSize + i + 1, title, imageUrl, rating, id, genres, year));
        }
    }
}
