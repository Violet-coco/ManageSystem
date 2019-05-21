package com.manage_system.ui.index.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.manage_system.R;
import com.manage_system.ui.index.adapter.SchoolNewsAdapter;
import com.manage_system.ui.manage.adapter.ChooseTitleAdapter;
import com.manage_system.utils.DateUtil;
import com.manage_system.utils.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SchoolNewsFragment extends Fragment {
    private String TAG = "SchoolNewsFragment";
    public List<Map<String,Object>> list=new ArrayList<>();

    public static Fragment newInstance() {
        SchoolNewsFragment fragment = new SchoolNewsFragment();
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
        SharedPreferences sp=getActivity().getSharedPreferences("processData", MODE_PRIVATE);
        Log.w(TAG,"**************");
        Log.w(TAG,sp.getString("news_list" , ""));
        JSONObject obj = JSON.parseObject(sp.getString("news_list" , ""));
        Log.w(TAG,"**************");
        Log.w(TAG,obj.toString());
        JSONArray array = new JSONArray(obj.getJSONObject("data").getJSONArray("newsList"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            Map<String, Object> map = new HashMap<>();
            map.put("time",DateUtil.getDateFormat(object.getString("sendDate")));
            map.put("title",object.getJSONObject("news").getString("title"));
            map.put("web_data",object.getJSONObject("news").getString("content"));
            JSONArray images = new JSONArray(object.getJSONObject("news").getJSONArray("imgList"));
            if(images.size()>0){
                map.put("image",object.getJSONObject("news").getJSONArray("imgList").toArray()[0]);
            }else{
                map.put("image","暂无图片");
            }
            map.put("sendName",object.getString("sendName"));
            map.put("news_data",object.toString());
            list.add(map);
        }
        recyclerView.setAdapter(new SchoolNewsAdapter(list));
        return rootView;
    }
}
