package com.qinjunyuan.zhihu.content;

import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

@FragmentScope
class ContentPresenter implements ContentContract.Presenter {
    private int id;
    private boolean type;
    private ContentContract.View view;
    private Repository repository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    ContentPresenter(int id, boolean type, ContentContract.View view, Repository repository) {
        this.id = id;
        this.type = type;
        this.view = view;
        this.repository = repository;
    }

    @Inject
    void setPresenter() {
        view.setPresenter(this);
    }

    @Override
    public void start() {
        compositeDisposable.add(repository.getExtra(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DailyExtra>() {
                    @Override
                    public void accept(@NonNull DailyExtra extra) throws Exception {
                        if (view.isActive()) {
                            view.upDateToolBar(extra);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (view.isActive()) {
                            view.makeToast();
                        }
                    }
                }));

        compositeDisposable.add(repository.getContent(id, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DailyContent>() {
                    @Override
                    public void accept(@NonNull DailyContent content) throws Exception {
                        if (view.isActive()) {
                            view.upDateWebView(content);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (view.isActive()) {
                            view.makeToast();
                        }
                    }
                }));
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "contentPresenter的onDestroy ");
        repository.clearExtra();
        compositeDisposable.clear();
        view = null;
    }
}
