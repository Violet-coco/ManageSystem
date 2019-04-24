package com.manage_system.ui.manage.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage_system.R;

public class ChooseTitleAdapter extends RecyclerView.Adapter<ChooseTitleAdapter.AuthorViewHolder> {

    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.ms_student_choose_title, parent, false);
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


    class AuthorViewHolder extends RecyclerView.ViewHolder {

        TextView mNickNameView;
        TextView mMottoView;
        public AuthorViewHolder(View itemView) {
            super(itemView);

//            mNickNameView = (TextView) itemView.findViewById(R.id.tv_nickname);
//            mMottoView = (TextView) itemView.findViewById(R.id.tv_motto);

        }
    }
}
