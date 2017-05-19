package com.qinjunyuan.zhihu.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DaggerRepositoryComponent;
import com.qinjunyuan.zhihu.data.RepositoryComponent;

public class MyApplication extends Application {
    private static MyApplication app;
    private RepositoryComponent repositoryComponent;
    private boolean networkState;

    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        setNetworkStatus();
        repositoryComponent = DaggerRepositoryComponent
                .builder()
                .appModule(new AppModule(getApplicationContext()))
                .utilModule(new UtilModule())
                .build();

    }

    public void setNetworkStatus() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        networkState = info != null && info.isAvailable();
    }

    public boolean getNetworkState() {
        return networkState;
    }

    public RepositoryComponent getRepositoryComponent() {
        return repositoryComponent;
    }
}
