package com.qinjunyuan.zhihu.main;

import com.qinjunyuan.zhihu.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
class MainActivityModule {
    private MainActivityContract.View view;

    MainActivityModule(MainActivityContract.View view) {
        this.view = view;
    }

    @Provides
    @ActivityScope
    MainActivityContract.View provideView() {
        return view;
    }
}
