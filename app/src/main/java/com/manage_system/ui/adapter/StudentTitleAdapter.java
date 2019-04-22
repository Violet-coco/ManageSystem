package com.manage_system.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.manage_system.R;
import com.manage_system.bean.FreshNewsBean;
import com.manage_system.utils.ImageLoaderUtil;

import java.util.List;

public class StudentTitleAdapter extends BaseQuickAdapter<FreshNewsBean.PostsBean, BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener {
    private Context mContext;

    public StudentTitleAdapter(Context context, @Nullable List<FreshNewsBean.PostsBean> data) {
//        super(R.layout.item_freshnews, data);
        super(R.layout.ms_student_choose_title);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, FreshNewsBean.PostsBean postsBean) {
        // 课题题目
        viewHolder.setText(R.id.tv_title, postsBean.getTitle());
        viewHolder.setText(R.id.tv_info, postsBean.getAuthor().getName());
        viewHolder.setText(R.id.tv_commnetsize, postsBean.getComment_count() + "评论");
        ImageLoaderUtil.LoadImage(mContext, postsBean.getCustom_fields().getThumb_c().get(0), (ImageView) viewHolder.getView(R.id.iv_logo));
       // setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
//        View view1 = baseQuickAdapter.getViewByPosition(i,R.id.iv_logo);
//        ReadActivity.launch(mContext, (FreshNewsBean.PostsBean) baseQuickAdapter.getItem(i));
    }
}
