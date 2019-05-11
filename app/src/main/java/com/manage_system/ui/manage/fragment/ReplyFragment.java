package com.manage_system.ui.manage.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manage_system.R;
import com.manage_system.ui.manage.adapter.ReplyAdapter;

import static android.content.Context.MODE_PRIVATE;

public class ReplyFragment extends Fragment {

    public static Fragment newInstance() {
        ReplyFragment fragment = new ReplyFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        SharedPreferences sp=getContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ReplyAdapter(sp.getString("authority","")));
        return rootView;
    }
}
