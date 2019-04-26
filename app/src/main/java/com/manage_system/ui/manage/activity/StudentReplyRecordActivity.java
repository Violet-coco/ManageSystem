package com.manage_system.ui.manage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.manage_system.R;

public class StudentReplyRecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton iv_back;
    Button reply_record_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ms_student_reply_record);
        getId();
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, StudentReplyRecordActivity.class);
    }

    /**
     * 获取id
     */
    private void getId()
    {
        iv_back = (ImageButton)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        reply_record_edit = (Button)findViewById(R.id.reply_record_edit);
        reply_record_edit.setOnClickListener(this);
    }

    public void onClick(View v) {//直接调用不会显示v被点击效果
        switch (v.getId()) {
            case R.id.iv_back:
                onViewClicked();
                break;
            case R.id.reply_record_edit:
                Intent intent = new Intent(StudentReplyRecordActivity.this,StudentReplyRecordEditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onViewClicked() {
        finish();
    }
}