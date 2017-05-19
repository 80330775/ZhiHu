package com.qinjunyuan.zhihu.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.util.MyApplication;
import com.qinjunyuan.zhihu.util.RxBus;

import javax.inject.Inject;


public class ContentActivity extends AppCompatActivity {
    @Inject
    ContentPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        boolean isDay = getSharedPreferences("themeMode", 0).getBoolean("isDay", true);
        if (isDay) {
            setTheme(R.style.ThemeDay);
        } else {
            setTheme(R.style.ThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_comment_activity);
        int id = getIntent().getIntExtra("id", -1);
        boolean isMainDaily = getIntent().getBooleanExtra("type", false);
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment fragment = (ContentFragment) fm.findFragmentById(R.id.fragment);
        if (fragment == null) {
            fragment = ContentFragment.newInstance(id, isMainDaily);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment, fragment);
            ft.commit();
        }
        DaggerContentPresenterComponent
                .builder()
                .repositoryComponent(((MyApplication) getApplication()).getRepositoryComponent())
                .contentPresenterModule(new ContentPresenterModule(fragment, id, isMainDaily))
                .build()
                .inject(this);
        i = getApplication().getSharedPreferences("size", 0).getInt("content_width", 0);
    }
    private int i;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (i == 0 && hasFocus) {
            RxBus.getInstance().post(2, true);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("tjy", "contentActivity的onDestroy ");
        super.onDestroy();
    }
}
