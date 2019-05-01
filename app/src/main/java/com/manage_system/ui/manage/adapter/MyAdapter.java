package com.manage_system.ui.manage.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.StudentChooseDoneTitleActivity;
import com.manage_system.ui.manage.activity.StudentChooseTitleMainActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.AuthorViewHolder> {
    private Context context;
    private String list;

    public MyAdapter(Context context, String list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_browse_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private static String TAG = "CHOOSE";
    class AuthorViewHolder extends RecyclerView.ViewHolder {

        public AuthorViewHolder(View itemView) {
            super(itemView);

        }
    }
}
