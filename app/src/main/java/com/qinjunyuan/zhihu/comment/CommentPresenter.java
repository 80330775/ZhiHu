package com.qinjunyuan.zhihu.comment;

import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.scope.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

@FragmentScope
public class CommentPresenter implements CommentContract.Presenter {
    private int id;
    private DailyExtra extra;
    private CommentContract.View view;
    private Repository repository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean getShortCommentIsComplete = false;

    @Inject
    CommentPresenter(int id, DailyExtra extra, CommentContract.View view, Repository repository) {
        this.id = id;
        this.extra = extra;
        this.view = view;
        this.repository = repository;
    }

    @Inject
    void setPresenter() {
        view.setPresenter(this);
    }

    @Override
    public void initRecyclerView() {
        view.initRecyclerView(repository.getCommentData());
    }

    @Override
    public void start() {
        compositeDisposable.add(repository.getLongComment(id, extra.getLong_comments(), extra.getShort_comments())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DailyComment.Comment>>() {
                    @Override
                    public void accept(@NonNull List<DailyComment.Comment> comments) throws Exception {
                        if (view.isActive()) {
                            view.upDateToolBar(extra.getComments());
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
                }));
    }

    @Override
    public void getShortComments() {
        if (!getShortCommentIsComplete) {
            compositeDisposable.add(repository.getShortComment(id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<DailyComment.Comment>>() {
                        @Override
                        public void accept(@NonNull List<DailyComment.Comment> comments) throws Exception {
                            if (view.isActive()) {
                                view.notifyDataSetChanged();
                                view.scrollToTop();
                                getShortCommentIsComplete = true;
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
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "commentPresenter的onDestroy ");
        repository.clearCommentData();
        compositeDisposable.clear();
        view = null;
    }
}
