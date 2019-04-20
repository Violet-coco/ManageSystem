package com.manage_system.ui.base;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.manage_system.utils.StringUtil;


/**带清除按钮EditText或TextView套件，如果输入为空则隐藏清除按钮
 * @use new TextClearSuit().addClearListener(...);
 */
public class TextClearSuit {
	private static final String TAG = "TextClearSuit";

	private TextView tv;
	private View clearView;
	
	private String inputedString;
	private int cursorPosition = 0;

	public TextView getTextView() {
		return tv;
	}
	public View getClearView() {
		return clearView;
	}
	public String getInputedString() {
		return inputedString;
	}
	public int getCursorPosition() {
		return cursorPosition;
	}


	public static final int BLANK_TYPE_DEFAULT = 0;
	public static final int BLANK_TYPE_TRIM = 1;
	public static final int BLANK_TYPE_NO_BLANK = 2;
	/**默认trim，隐藏方式为gone
	 * @param tv
	 * @param clearView
	 */
	public void addClearListener(final TextView tv, final View clearView) {
		addClearListener(tv, BLANK_TYPE_TRIM, clearView, false);
	}
	/**默认隐藏方式为gone
	 * @param tv
	 * @param trim
	 * @param clearView
	 */
	public void addClearListener(final TextView tv, final int blankType, final View clearView) {
		addClearListener(tv, blankType, clearView, false);
	}
	/**
	 * @param tv 输入框
	 * @param trim et内容前后是否不能含有空格
	 * @param clearView 清除输入框内容按钮
	 * @param isClearViewInvisible  如果et输入为空，隐藏clearView的方式为gone(false)还是invisible(true)
	 */
	public void addClearListener(final TextView tv, final int blankType, final View clearView, final boolean isClearViewInvisible) {
		if (tv == null || clearView == null) {
			Log.e(TAG, "addClearListener  (tv == null || clearView == null)  >> return;");
			return;
		}

		this.tv = tv;
		this.clearView = clearView;
		if (tv.getText() != null) {
			inputedString = tv.getText().toString();
		}

		clearView.setVisibility(StringUtil.isNotEmpty(tv, false) ? View.VISIBLE : View.GONE);
		clearView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv.setText("");
				tv.requestFocus();
			}
		});
		tv.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s == null || StringUtil.isNotEmpty(s.toString(), false) == false) {
					inputedString = "";
					if (isClearViewInvisible == false) {
						clearView.setVisibility(View.GONE);
					} else {
						clearView.setVisibility(View.INVISIBLE);
					}
				} else {
					inputedString = "" + s.toString();
//					if (inputedString.contains(" ")) {//stackoverflow
//						if (blankType == BLANK_TYPE_TRIM) {
//							inputedString = inputedString.trim();
//						} else if (blankType == BLANK_TYPE_NO_BLANK) {
//							inputedString = inputedString.replaceAll(" ", "");
//						}
//						tv.setText(inputedString);
//						tv.setSelection(inputedString.length());
//					}
					clearView.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	public interface onTextChangedListener {
		void onTextChanged(CharSequence s, int start, int before, int count);
	}

}
