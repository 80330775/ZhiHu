package com.qinjunyuan.zhihu.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.main.main.MainDailyFragment;
import com.qinjunyuan.zhihu.main.theme.ThemeDailyFragment;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.util.MyApplication;
import com.qinjunyuan.zhihu.util.RxBus;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements MainActivityContract.View, MainActivityRecyclerViewAdapter.OnItemClickListener {
    private boolean isDay, isMain = true;
    private NetworkChangeReceiver networkChangeReceiver;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MainActivityRecyclerViewAdapter adapter;
    private FragmentManager fm;
    private int currentPosition;
    private static final String MAIN = "main", THEME = "theme";

    @Inject
    MainActivityPresenter presenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        SharedPreferences sp = getSharedPreferences("themeMode", 0);
        boolean isDay = sp.getBoolean("isDay", true);
        MenuItem item = menu.findItem(R.id.themeMode);
        if (isDay) {
            item.setTitle(R.string.nightMode);
        } else {
            item.setTitle(R.string.dayMode);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isMain) {
            menu.findItem(R.id.notifications).setVisible(true);
            menu.findItem(R.id.themeMode).setVisible(true);
            menu.findItem(R.id.setting).setVisible(true);
        } else {
            menu.findItem(R.id.notifications).setVisible(false);
            menu.findItem(R.id.themeMode).setVisible(false);
            menu.findItem(R.id.setting).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                notifyDataSetChanged();
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.themeMode:
                SharedPreferences sp = getSharedPreferences("themeMode", 0);
                isDay = !sp.getBoolean("isDay", true);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isDay", isDay);
                editor.apply();
                if (isDay) {
                    item.setTitle(R.string.nightMode);
                    toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar));
                    ColorDrawable drawable = new ColorDrawable(ContextCompat.getColor(this, R.color.grey_1));
                    getWindow().setBackgroundDrawable(drawable);
                } else {
                    item.setTitle(R.string.dayMode);
                    toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_13));
                    ColorDrawable drawable = new ColorDrawable(ContextCompat.getColor(this, R.color.grey_12));
                    getWindow().setBackgroundDrawable(drawable);
                }
                adapter.setThemeMode(isDay);
                notifyDataSetChanged();
                ((MainDailyFragment) fm.findFragmentByTag(MAIN)).setThemeMode(isDay);
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ThemeDailyFragment themeFragment = (ThemeDailyFragment) fm.findFragmentByTag(THEME);
        MainDailyFragment mainFragment = (MainDailyFragment) fm.findFragmentByTag(MAIN);
        if (mainFragment != null && mainFragment.isVisible()) {
            isMain = true;
            Log.d("tjy", "保存状态 main isVisible() " + mainFragment.isVisible());
        }
        if (themeFragment != null && themeFragment.isVisible()) {
            isMain = false;
            Log.d("tjy", "保存状态 theme isVisible() " + themeFragment.isVisible());
        }
        outState.putBoolean("isMain", isMain);
        outState.putCharSequence("title", toolbar.getTitle());
        outState.putInt("position", adapter.getCurrentPosition());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("themeMode", 0);
        isDay = sp.getBoolean("isDay", true);
        if (isDay) {
            setTheme(R.style.ThemeDay);
        } else {
            setTheme(R.style.ThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        DaggerMainActivityComponent
                .builder()
                .repositoryComponent(((MyApplication) getApplication()).getRepositoryComponent())
                .mainActivityModule(new MainActivityModule(this))
                .build()
                .inject(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, filter);

        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (savedInstanceState != null) {
            isMain = savedInstanceState.getBoolean("isMain");
            if (isMain) {
                addFragment(ft, MAIN);
            } else {
                addFragment(ft, THEME);
            }
            toolbar.setTitle(savedInstanceState.getCharSequence("title"));
            currentPosition = savedInstanceState.getInt("position");
        } else {
            addFragment(ft, MAIN);
        }
        RxBus.getInstance().addDisposable(this, RxBus.getInstance().toObservable(0, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        toolbar.setTitle(s);
                    }
                }));
        presenter.initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        presenter.onDestroy();
        RxBus.getInstance().dispose(this);
        RxBus.getInstance().dispose(adapter);
        Log.d("tjy", "mainActivity的onDestroy ");
        super.onDestroy();
    }

    @Override
    public void initRecyclerView(List<SkidMenu.Info> dataList) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new MainActivityRecyclerViewAdapter(dataList, this);
        adapter.setThemeMode(isDay);
        if (currentPosition != 0) {
            adapter.setCurrentPosition(currentPosition);
        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int id, String name) {
        toolbar.setTitle(name);
        if (id != 1) {
            themeFragmentSelector(id);
            isMain = false;
        } else {
            showMainFragment();
            isMain = true;
        }
        invalidateOptionsMenu();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void themeFragmentSelector(int id) {
        ThemeDailyFragment themeFragment = (ThemeDailyFragment) fm.findFragmentByTag(THEME);
        MainDailyFragment mainFragment = (MainDailyFragment) fm.findFragmentByTag(MAIN);
        if (themeFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            themeFragment = new ThemeDailyFragment();
            themeFragment.setId(id);
            ft.hide(mainFragment);
            ft.add(R.id.fragment, themeFragment, THEME);
            ft.commit();
            return;
        }
        if (!themeFragment.isVisible()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mainFragment);
            ft.show(themeFragment);
            ft.commit();
        }
        themeFragment.setId(id);
        themeFragment.setThemeMode(isDay);
        themeFragment.start();
    }

    private void showMainFragment() {
        ThemeDailyFragment themeFragment = (ThemeDailyFragment) fm.findFragmentByTag(THEME);
        MainDailyFragment mainFragment = (MainDailyFragment) fm.findFragmentByTag(MAIN);
        if (themeFragment != null && themeFragment.isVisible()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(themeFragment);
            ft.show(mainFragment);
            ft.commit();
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        ThemeDailyFragment themeFragment = (ThemeDailyFragment) fm.findFragmentByTag(THEME);
        MainDailyFragment mainFragment = (MainDailyFragment) fm.findFragmentByTag(MAIN);
        if (themeFragment != null && themeFragment.isVisible()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(themeFragment);
            if (mainFragment != null) {
                ft.show(mainFragment);
                ft.commit();
            } else {
                addFragment(ft, MAIN);
            }
            toolbar.setTitle("首页");
            isMain = true;
            invalidateOptionsMenu();
            adapter.setCurrentPosition(1);
            return;
        }
        super.onBackPressed();
        finish();
    }

    private void addFragment(FragmentTransaction ft, String type) {
        Fragment fragment = fm.findFragmentByTag(type);
        Log.d("tjy", "themeFragment为null是 " + String.valueOf(fragment == null) + " 的");
        if (fragment == null) {
            if (type.equals(MAIN)) {
                fragment = new MainDailyFragment();
            } else {
                fragment = new ThemeDailyFragment();
            }
            ft.add(R.id.fragment, fragment, type);
            ft.commit();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void makeToast() {
        Toast.makeText(this, "无法获取数据，请检查网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isFinishing();
    }

    @Override
    public void download() {
        presenter.download(getApplicationContext());
    }

    static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyApplication.getInstance().setNetworkStatus();
        }
    }
}