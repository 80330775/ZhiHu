package com.qinjunyuan.zhihu.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.qinjunyuan.zhihu.data.local.MySQLite;
import com.qinjunyuan.zhihu.data.remote.ZhiHuAPI;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class UtilModule {
    @Provides
    @Singleton
    ZhiHuAPI provideZhiHuAPI() {
        return new Retrofit.Builder()
                .baseUrl("http://news-at.zhihu.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ZhiHuAPI.class);
    }

    @Provides
    @Singleton
    SQLiteDatabase provideSQLite(Context context) {
        return new MySQLite(context).getReadableDatabase();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences("ZhiHu", 0);
    }
}
