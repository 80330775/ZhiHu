package com.qinjunyuan.zhihu.main;

import com.qinjunyuan.zhihu.data.RepositoryComponent;
import com.qinjunyuan.zhihu.scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = RepositoryComponent.class, modules = MainActivityModule.class)
interface MainActivityComponent {
    void inject(MainActivity activity);
}
