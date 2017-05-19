package com.qinjunyuan.zhihu.main.main;

import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataManager;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.scope.FragmentScope;
import com.qinjunyuan.zhihu.util.MyApplication;


import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

@FragmentScope
class MainDailyPresenter implements MainDailyContract.Presenter {
    private MainDailyContract.View view;
    private Repository repository;
    private Disposable disposable;

    @Inject
    MainDailyPresenter(MainDailyContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void start() {
        getLatestDailyList();
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "mainPresenter的onDestroy ");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        view = null;
    }

    @Override
    public void initRecyclerView() {
        view.initRecyclerView(repository.getStoryList());
    }

    @Override
    public void getLatestDailyList() {
        disposable = repository.getLatestDailies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MainDailies.Bean>() {
                    @Override
                    public void accept(@NonNull MainDailies.Bean bean) throws Exception {
                        if (view.isActive()) {
                            view.initViewPager(repository.getTopStoryList());
                            view.notifyDataSetChanged();
                            view.setRefreshing(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (view.isActive()) {
                            view.makeToast();
                        }
                    }
                });
    }

    @Override
    public void getBeforeDailies() {
        repository.getBeforeDailies(null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MainDailies.Bean>() {
                    @Override
                    public void accept(@NonNull MainDailies.Bean bean) throws Exception {
                        if (view.isActive()) {
                            view.notifyItemRangeInserted(bean.getUpDateCount());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (view.isActive()) {
                            view.makeToast();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        if (MyApplication.getInstance().getNetworkState()) {
            repository.clearMainData();
        }
        getLatestDailyList();
    }
}
