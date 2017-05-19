package com.qinjunyuan.zhihu.comment;

import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class CommentPresenterModule {
    private CommentContract.View view;
    private int id;
    private DailyExtra extra;

    public CommentPresenterModule(CommentContract.View view, int id, DailyExtra extra) {
        this.view = view;
        this.id = id;
        this.extra = extra;
    }

    @Provides
    @FragmentScope
    CommentContract.View provideView() {
        return view;
    }

    @Provides
    @FragmentScope
    int provideId() {
        return id;
    }

    @Provides
    @FragmentScope
    DailyExtra provideExtra() {
        return extra;
    }
}
