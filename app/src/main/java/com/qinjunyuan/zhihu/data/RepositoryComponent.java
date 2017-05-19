package com.qinjunyuan.zhihu.data;

import com.qinjunyuan.zhihu.util.AppModule;
import com.qinjunyuan.zhihu.util.UtilModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, UtilModule.class})
public interface RepositoryComponent {
    Repository provideRepository();
}
