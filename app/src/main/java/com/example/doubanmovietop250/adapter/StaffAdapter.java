package com.example.doubanmovietop250.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.doubanmovietop250.R;
import com.example.doubanmovietop250.activity.StaffInfoActivity;
import com.example.doubanmovietop250.item.StaffItem;

import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {

    private List<StaffItem> staffList;

    private Context context;
;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout staffLayout;
        ImageView staffImage;
        TextView staffName;

        public ViewHolder(View view) {
            super(view);
            staffLayout = (LinearLayout) view.findViewById(R.id.staff_item_layout);
            staffImage = (ImageView) view.findViewById(R.id.staff_image);
            staffName = (TextView) view.findViewById(R.id.staff_name);
        }
    }

    public StaffAdapter(List<StaffItem> staffList) {
        this.staffList = staffList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.staffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String id = staffList.get(position).getId();
                StaffInfoActivity.actionStart(context, id);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StaffItem staffItem = staffList.get(position);
        Glide.with(context).load(staffItem.getImageUrl()).into(holder.staffImage);
        holder.staffName.setText(staffItem.getName());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }
}
