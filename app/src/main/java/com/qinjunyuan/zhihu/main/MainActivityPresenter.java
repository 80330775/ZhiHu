package com.qinjunyuan.zhihu.main;


import android.content.Context;
import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.data.DataManager;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.scope.ActivityScope;
import com.qinjunyuan.zhihu.util.MyApplication;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@ActivityScope
class MainActivityPresenter implements MainActivityContract.Presenter {
    private MainActivityContract.View view;
    private Repository repository;
    private Disposable disposable, disposable1;

    @Inject
    MainActivityPresenter(MainActivityContract.View view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void start() {
        disposable = repository.getSkidMenu()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SkidMenu.Info>>() {
                    @Override
                    public void accept(@NonNull List<SkidMenu.Info> list) throws Exception {
                        if (!view.isActive()) {
                            view.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void initRecyclerView() {
        view.initRecyclerView(repository.getSkidMenuData());
    }

    @Override
    public void download(Context context) {
        if (MyApplication.getInstance().getNetworkState()) {
            disposable1 = repository.offlineDownload(context);
        } else {
            view.makeToast();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "mainActivityPresenter的onDestroy ");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (disposable1 != null) {
            disposable1.dispose();
        }
        view = null;
    }
}
