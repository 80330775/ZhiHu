package com.qinjunyuan.zhihu.content;

import com.qinjunyuan.zhihu.data.RepositoryComponent;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = RepositoryComponent.class, modules = ContentPresenterModule.class)
interface ContentPresenterComponent {
    void inject(ContentActivity activity);
}
