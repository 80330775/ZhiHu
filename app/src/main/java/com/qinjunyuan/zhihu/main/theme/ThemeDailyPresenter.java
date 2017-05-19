package com.qinjunyuan.zhihu.main.theme;


import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@FragmentScope
class ThemeDailyPresenter implements ThemeDailyContract.Presenter {
    private int id;
    private ThemeDailyContract.View view;
    private Repository repository;
    private Disposable disposable;

    @Inject
    ThemeDailyPresenter(int id, ThemeDailyContract.View view, Repository repository) {
        this.id = id;
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void initRecyclerView() {
        view.initRecyclerView(repository.getThemeDailies());
    }

    @Override
    public void start() {
        disposable = repository.getThemeDailies(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ThemeDailies>>() {
                    @Override
                    public void accept(@NonNull List<ThemeDailies> list) throws Exception {
                        if (view.isActive()) {
                            view.notifyDataSetChanged();
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
    public void onDestroy() {
        Log.d("tjy", "themePresenter的onDestroy ");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        view = null;
    }
}
