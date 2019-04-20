package com.manage_system.component;

import com.manage_system.ui.browse.JdDetailFragment;
import com.manage_system.ui.index.ArticleReadActivity;
import com.manage_system.ui.index.ImageBrowseActivity;
import com.manage_system.ui.index.IndexFragment;
import com.manage_system.ui.manage.DetailFragment;
import com.manage_system.ui.manage.ManageFragment;

import dagger.Component;

/**
 * desc: .
 * author: Will .
 * date: 2017/9/2 .
 */
@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(ManageFragment manageFragment);

    void inject(DetailFragment detailFragment);

    void inject(JdDetailFragment jdDetailFragment);

    void inject(ImageBrowseActivity imageBrowseActivity);

    void inject( com.manage_system.ui.index.DetailFragment detailFragment);

    void inject(ArticleReadActivity articleReadActivity);

    void inject(IndexFragment indexFragment);

}
