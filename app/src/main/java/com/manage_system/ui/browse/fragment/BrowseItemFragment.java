package com.manage_system.ui.browse.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.net.ApiConstants;
import com.manage_system.ui.browse.adapter.BrowseAdapter;
import com.manage_system.ui.manage.activity.manager.ManagerCtCheckMainActivity;
import com.manage_system.ui.manage.adapter.ChooseTitleAdapter;
import com.manage_system.ui.manage.adapter.LeaderAdapter;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.OkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class BrowseItemFragment extends Fragment {
    private String TAG = "BrowseItemFragment";
    public List<Map<String,Object>> list=new ArrayList<>();

    public static Fragment newInstance() {
        BrowseItemFragment fragment = new BrowseItemFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        SharedPreferences sp=getContext().getSharedPreferences("processData", MODE_PRIVATE);
        JSONObject obj = JSON.parseObject(sp.getString("exe_list" , ""));
        JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("excellents"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            Log.e(TAG, object.toString());
            Map<String, Object> map = new HashMap<>();
            map.put("sName",object.getString("sName"));
            map.put("pTitle",object.getString("pTitle"));
            map.put("sid",object.getString("sid"));
            map.put("sMaj",object.getString("sMaj"));
            map.put("grade",object.getString("grade"));
            map.put("excellents_data",object.toString());
            list.add(map);
        }
        recyclerView.setAdapter(new BrowseAdapter(list));
        return rootView;
    }



}
