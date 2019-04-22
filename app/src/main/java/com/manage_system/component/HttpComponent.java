package com.manage_system.component;

import com.manage_system.ui.manage.JdDetailFragment;
import com.manage_system.ui.index.ArticleReadActivity;
import com.manage_system.ui.index.ImageBrowseActivity;
import com.manage_system.ui.index.IndexFragment;
import com.manage_system.ui.browse.DetailFragment;
import com.manage_system.ui.browse.BrowseFragment;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(BrowseFragment browseFragment);

    void inject(DetailFragment detailFragment);

    void inject(JdDetailFragment jdDetailFragment);

    void inject(ImageBrowseActivity imageBrowseActivity);

    void inject( com.manage_system.ui.index.DetailFragment detailFragment);

    void inject(ArticleReadActivity articleReadActivity);

    void inject(IndexFragment indexFragment);

}
