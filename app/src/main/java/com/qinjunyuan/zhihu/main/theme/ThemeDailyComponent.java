package com.qinjunyuan.zhihu.main.theme;

import com.qinjunyuan.zhihu.data.RepositoryComponent;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = RepositoryComponent.class, modules = ThemeDailyModule.class)
public interface ThemeDailyComponent {
    void inject(ThemeDailyFragment fragment);
}
