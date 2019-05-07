package com.manage_system.utils;

import android.widget.EditText;

public class ViewStatusUtils {

    public void setEnableEdit(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }

    public void setDisableEdit(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
    }
}
