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
import android.widget.ImageButton;

import com.manage_system.R;
import com.manage_system.ui.manage.adapter.ChooseTitleAdapter;

import static android.content.Context.MODE_PRIVATE;

public class ChooseTitleFragment extends Fragment {

    public static Fragment newInstance() {
        ChooseTitleFragment fragment = new ChooseTitleFragment();
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
        recyclerView.setAdapter(new ChooseTitleAdapter(sp.getString("authority","")));
        return rootView;
    }
}
