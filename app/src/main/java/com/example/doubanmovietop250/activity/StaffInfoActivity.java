package com.example.doubanmovietop250.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.doubanmovietop250.R;

public class StaffInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_info);
        Intent intent = getIntent();
        // 获取影人的id，用于构建WebView的url
        String id = intent.getStringExtra("staff_id");
        // 更改标题栏为Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        // 增加标题栏左侧的返回箭头
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        WebView webView = (WebView) findViewById(R.id.web_view);
        // 启用JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // 打开影人详细信息的网页
        webView.loadUrl("https://movie.douban.com/celebrity/" + id + "/mobile");
    }

    // 用于启动这个Activity
    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, StaffInfoActivity.class);
        intent.putExtra("staff_id", id);
        context.startActivity(intent);
    }

    // 设置菜单item的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 销毁当前活动
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
