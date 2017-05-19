package com.qinjunyuan.zhihu.comment;

import com.qinjunyuan.zhihu.data.RepositoryComponent;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = RepositoryComponent.class, modules = CommentPresenterModule.class)
public interface CommentPresenterComponent {
    void inject(CommentActivity activity);
}
