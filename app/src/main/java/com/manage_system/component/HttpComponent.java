package com.manage_system.component;

import com.manage_system.ui.browse.BrowseFragment1;
import com.manage_system.ui.index.ArticleReadActivity;
import com.manage_system.ui.index.ImageBrowseActivity;
import com.manage_system.ui.index.IndexFragment1;
import com.manage_system.ui.browse.DetailFragment;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface HttpComponent {

    void inject(BrowseFragment1 browseFragment1);

    void inject(DetailFragment detailFragment);

    void inject(ImageBrowseActivity imageBrowseActivity);

    void inject( com.manage_system.ui.index.DetailFragment detailFragment);

    void inject(ArticleReadActivity articleReadActivity);

    void inject(IndexFragment1 indexFragment1);

}
