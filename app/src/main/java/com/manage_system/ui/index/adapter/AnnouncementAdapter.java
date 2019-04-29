package com.manage_system.ui.index.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.manage_system.R;
import com.manage_system.ui.manage.activity.StudentChooseDoneTitleActivity;
import com.manage_system.ui.manage.activity.StudentChooseTitleActivity;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AuthorViewHolder> {

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_browse_item, parent, false);
        AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {

//        holder.student_choose_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(v.getContext(),StudentChooseTitleActivity.class);
//                v.getContext().startActivity(intent);
//            }
//        });
//
//        holder.student_choose_done_title.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(v.getContext(),StudentChooseDoneTitleActivity.class);
//                v.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    private static String TAG = "CHOOSE";
    class AuthorViewHolder extends RecyclerView.ViewHolder {
//        private LinearLayout student_choose_title;
//        private LinearLayout student_choose_done_title;
        public AuthorViewHolder(View itemView) {
            super(itemView);
//            student_choose_title = (LinearLayout) itemView.findViewById(R.id.student_choose_title);
//            student_choose_done_title = (LinearLayout) itemView.findViewById(R.id.student_choose_done_title);
        }
    }
}
