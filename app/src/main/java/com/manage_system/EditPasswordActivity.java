package com.manage_system;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EditPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);
    }

    /**启动这个Activity的Intent
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, EditPasswordActivity.class);
    }
}