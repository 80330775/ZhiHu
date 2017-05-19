package com.qinjunyuan.zhihu.main.main;

import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MainDailyModule {
    private MainDailyContract.View view;

    public MainDailyModule(MainDailyContract.View view) {
        this.view = view;
    }

    @Provides
    @FragmentScope
    MainDailyContract.View provideFragment(){
        return view;
    }
}
