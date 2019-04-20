
package com.manage_system.ui.inter;

import android.app.Activity;

/**Fragment的逻辑接口
 * @author Lemon
 * @use implements FragmentPresenter
 * @warn 对象必须是Fragment
 */
public interface FragmentPresenter extends Presenter {

	static final String ARGUMENT_ID = "ARGUMENT_ID";
	static final String ARGUMENT_USER_ID = "ARGUMENT_USER_ID";

	static final int RESULT_OK = Activity.RESULT_OK;
	static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
}