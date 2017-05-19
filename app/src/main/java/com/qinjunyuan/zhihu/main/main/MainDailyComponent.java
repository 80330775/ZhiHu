package com.qinjunyuan.zhihu.main.main;

import com.qinjunyuan.zhihu.data.RepositoryComponent;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = RepositoryComponent.class, modules = MainDailyModule.class)
public interface MainDailyComponent {
    void inject(MainDailyFragment fragment);
}
