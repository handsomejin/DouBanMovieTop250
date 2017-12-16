package com.example.doubanmovietop250.wrapper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doubanmovietop250.R;

// LoadMoreWrapper是套在MovieListAdapter外面的另一个Adapter
// 以在原有Adapter的基础上增加对FootView的显示
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView.Adapter adapter;

    // 普通布局
    private final int TYPE_ITEM = 1;

    // 脚布局
    private final int TYPE_FOOTER = 2;

    // 当前加载状态，默认为加载完成
    private int loadState = 2;

    // 正在加载
    public final int LOADING = 1;

    // 加载完成
    public final int LOADING_COMPLETE = 2;

    // 加载到底
    public final int LOADING_END = 3;

    public LoadMoreWrapper(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        // 如果是最后一个item，则设置为脚布局，否则为普通布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    // 创建ViewHolder实例
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("LoadMoreWrapper", "onCreateViewHolder called");
        // 根据item类型的不同，分别创建不同的View
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.load_more_footer, parent, false);
            return new FootViewHolder(view);
        } else {
            // 调用MovieListAdapter的onCreateViewHolder方法来创建普通类型的View
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    // 对RecyclerView的每个item中的数据进行赋值
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d("LoadMoreWrapper", "onBindViewHolder called");
        // 如果holder是FootViewHolder的实例
        if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING:
                    // pbLoading对应ProgressBar
                    // tvLoading对应TextView
                    // llEnd对应LinearLayout
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE:
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END:
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        } else {
            // 调用MovieListAdapter的onBindViewHolder方法来对普通类型的View进行赋值
            adapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("LoadMoreWrapper", "getItemCount called");
        return adapter.getItemCount() + 1;
    }

    // 脚布局的ViewHolder
    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    // 设置加载状态
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }
}