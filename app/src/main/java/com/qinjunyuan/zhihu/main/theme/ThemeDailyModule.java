package com.qinjunyuan.zhihu.main.theme;

import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ThemeDailyModule {
    private int id;
    private ThemeDailyContract.View view;

    ThemeDailyModule(int id, ThemeDailyContract.View view) {
        this.id = id;
        this.view = view;
    }

    @Provides
    @FragmentScope
    ThemeDailyContract.View provideView() {
        return view;
    }

    @Provides
    @FragmentScope
    int provideId() {
        return id;
    }
}
