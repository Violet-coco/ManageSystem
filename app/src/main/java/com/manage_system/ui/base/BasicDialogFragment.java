package com.manage_system.ui.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manage_system.R;

/**
 * Description:基础对话框
 * User: xjp
 * Date: 2015/5/20
 * Time: 8:44
 */

public class BasicDialogFragment extends DialogFragment {

    public static BasicDialogFragment newInstance(int style) {
        BasicDialogFragment dialogFragment = new BasicDialogFragment();

        Bundle bundle = new Bundle();
//        bundle.putInt(style, style);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        int styleNum = getArguments().getInt(style, 0);
        int style = 0;
//        switch (styleNum) {
//            case 0:
//                style = DialogFragment.STYLE_NORMAL;//默认样式
//                break;
//            case 1:
//                style = DialogFragment.STYLE_NO_TITLE;//无标题样式
//                break;
//            case 2:
//                style = DialogFragment.STYLE_NO_FRAME;//无边框样式
//                break;
//            case 3:
//                style = DialogFragment.STYLE_NO_INPUT;//不可输入，不可获得焦点样式
//                break;
//        }
        setStyle(style, 0);//设置样式

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        getDialog().setTitle(退出微信);//添加标题
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        View view = inflater.inflate(R.layout.alert_dialog, container);

        TextView title = (TextView) view.findViewById(R.id.tvAlertDialogTitle);
        title.setText("微信退出");
        TextView message = (TextView) view.findViewById(R.id.tvAlertDialogMessage);
        message.setText("是否退出微信，退出微信之后不能受到消息。是否退出微信，退出微信之后不能受到消息。");
        view.findViewById(R.id.btnAlertDialogNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btnAlertDialogPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
