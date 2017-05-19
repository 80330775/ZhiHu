package com.qinjunyuan.zhihu.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.util.MyApplication;

import javax.inject.Inject;


public class CommentActivity extends AppCompatActivity {
    @Inject
    CommentPresenter presenter;

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
        int id = getIntent().getIntExtra("id", 0);
        DailyExtra extra = (DailyExtra) getIntent().getSerializableExtra("extra");
        FragmentManager fm = getSupportFragmentManager();
        CommentFragment fragment = (CommentFragment) fm.findFragmentById(R.id.fragment);
        if (fragment == null) {
            fragment = CommentFragment.newInstance(isDay);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment, fragment);
            ft.commit();
        }
        DaggerCommentPresenterComponent
                .builder()
                .repositoryComponent(((MyApplication) getApplication()).getRepositoryComponent())
                .commentPresenterModule(new CommentPresenterModule(fragment, id, extra))
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("tjy", "commentActivity的onDestroy ");
        super.onDestroy();
    }
}
