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

    // 标记当前是否正在加载，防止在短时间加载多次
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // 更改标题栏为Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 配置RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MovieListAdapter adapter = new MovieListAdapter(movieList);
        loadMoreWrapper = new LoadMoreWrapper(adapter);
        recyclerView.setAdapter(loadMoreWrapper);
        // 对电影列表初始化
        requestMovieList();
        // 对swipeRefresh进行配置
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 调用刷新函数
                refreshMovieItem();
            }
        });
        // 对recyclerView设置滑动监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                // 如果现在不在加载
                if (!isLoading) {
                    // 设置loadMoreWrapper当前正在加载
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);
                    isLoading = true;
                    if (movieList.size() <= 225) {
                        // 如果还有数据可以加载，就调用加载函数
                        loadMovieItem();
                    } else {
                        // 否则没有数据可以加载，加载到底了
                        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                        isLoading = false;
                    }
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // 注册fab的点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 滑动到recyclerView的第一项
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    // 对电影列表初始化
    private void requestMovieList() {
        // Android在主线程不能执行http请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    movieList.clear();
                    // 执行网络请求并且将数据解析后放入movieList中
                    sendRequestAndParse(movieList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在主线程中通知数据发生了变化
                            loadMoreWrapper.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 刷新函数
    private void refreshMovieItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // poiMovieList是暂存数据的数组
                    poiMovieList.clear();
                    sendRequestAndParse(poiMovieList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int previousSize = movieList.size();
                            movieList.clear();
                            // 防止在刷新时快速向上滚动，抛出IndexOutOfBoundsException
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

    // 加载函数
    private void loadMovieItem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendRequestAndParse(movieList);
                    // 在sendRequestAndParse()函数调用完成后设置isLoading为false
                    isLoading = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在主线程中设置loadMoreWrapper当前已经加载完成
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

    // 执行网络请求并且将数据解析后放入list中
    private void sendRequestAndParse(List<MovieItem> list) throws Exception {
        // 获取数组大小，用于构建用于request的url
        int listSize = list.size();
        // 使用OkHttp进行http请求
        // 创建OkHttpClient的实例
        OkHttpClient client = new OkHttpClient();
        // 创建一个Request对象
        Request request = new Request.Builder()
                .url("http://api.douban.com/v2/movie/top250?start=" + listSize + "&count=25")
                .build();
        // 用Response对象来接收服务器返回的数据
        Response response = client.newCall(request).execute();
        // 获取返回的具体内容
        String responseData = response.body().string();
        // 将JSON数据解析为一个MovieListBean对象
        Gson gson = new Gson();
        MovieListBean movieListBean = gson.fromJson(responseData, MovieListBean.class);
        // 将MovieListBean对象中的数据放入list中
        for (int i = 0; i < 25; i++) {
            String title = movieListBean.getSubjects().get(i).getTitle();
            String imageUrl = movieListBean.getSubjects().get(i).getImages().getSmall();
            float rating = movieListBean.getSubjects().get(i).getRating().getAverage();
            String id = movieListBean.getSubjects().get(i).getId();
            // 将类型List中的全部数据取得后合并为一个类型String
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
            // 将新项添加到list中
            list.add(new MovieItem(
                    listSize + i + 1, title, imageUrl, rating, id, genres, year));
        }
    }
}
