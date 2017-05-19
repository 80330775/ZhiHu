package com.qinjunyuan.zhihu.content;

import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
class ContentPresenterModule {
    private ContentContract.View view;
    private int id;
    private boolean type;

    ContentPresenterModule(ContentContract.View view, int id, boolean type) {
        this.view = view;
        this.id = id;
        this.type = type;
    }

    @Provides
    @FragmentScope
    ContentContract.View provideView() {
        return view;
    }

    @Provides
    @FragmentScope
    int provideId() {
        return id;
    }

    @Provides
    @FragmentScope
    boolean provideType() {
        return type;
    }
}
