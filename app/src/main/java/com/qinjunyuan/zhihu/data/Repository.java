package com.qinjunyuan.zhihu.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.data.local.LocalDataSource;
import com.qinjunyuan.zhihu.data.remote.RemoteDataSource;
import com.qinjunyuan.zhihu.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@Singleton
public class Repository implements DataManager {
    private LocalDataSource local;
    private RemoteDataSource remote;

    private List<SkidMenu.Info> skidMenuData = new ArrayList<>();

    private String date;
    private List<MainDailies.TopStory> topStoryList = new ArrayList<>();
    private List<MainDailies.Story> storyList = new ArrayList<>();

    private List<ThemeDailies> themeDailies = new ArrayList<>();
    private SparseArray<List<ThemeDailies>> map = new SparseArray<>(12);

    private List<DailyComment.Comment> commentData = new ArrayList<>();
    private DailyExtra extra;

    @Inject
    public Repository(LocalDataSource local, RemoteDataSource remote) {
        this.local = local;
        this.remote = remote;
    }

    public Disposable offlineDownload(final Context context) {
        return remote.downloadLatestDaily1()
                .subscribe(new Consumer<MainDailies>() {
                    @Override
                    public void accept(@NonNull MainDailies mainDailies) throws Exception {
                        local.saveOfflineDownload(remote.downloadLatestDaily2(mainDailies), context);
                    }
                });
    }

    @Override
    public Observable<List<SkidMenu.Info>> getSkidMenu() {
        if (!skidMenuData.isEmpty()) {
            return Observable.just(skidMenuData);
        }
        return local.getSkidMenu()
                .doOnNext(new Consumer<List<SkidMenu.Info>>() {
                    @Override
                    public void accept(@NonNull List<SkidMenu.Info> list) throws Exception {
                        skidMenuData.addAll(list);
                    }
                })
                .switchIfEmpty(remote.getSkidMenu()
                        .doOnNext(new Consumer<List<SkidMenu.Info>>() {
                            @Override
                            public void accept(@NonNull List<SkidMenu.Info> list) throws Exception {
                                skidMenuData.addAll(list);
                                local.saveSkidMenu(list);
                            }
                        }));
    }

    @Override
    public Single<MainDailies.Bean> getLatestDailies() {
        if (!storyList.isEmpty() && !topStoryList.isEmpty()) {
            return Single.just(new MainDailies.Bean());
        }
        return remote.getLatestDailies()
                .doOnSuccess(new Consumer<MainDailies.Bean>() {
                    @Override
                    public void accept(@NonNull MainDailies.Bean bean) throws Exception {
                        Repository.this.date = bean.getDate();
                        Repository.this.storyList.addAll(bean.getStoryList());
                        Repository.this.topStoryList.addAll(bean.getTopStoryList());
                        local.saveLatestDailyList(bean);
                    }
                })
                .onErrorResumeNext(local.getLatestDailies().doOnSuccess(new Consumer<MainDailies.Bean>() {
                    @Override
                    public void accept(@NonNull MainDailies.Bean bean) throws Exception {
                        Repository.this.date = bean.getDate();
                        Repository.this.storyList.addAll(bean.getStoryList());
                        Repository.this.topStoryList.addAll(bean.getTopStoryList());
                    }
                }));
    }

    @Override
    public Single<MainDailies.Bean> getBeforeDailies(String date) {
        if (!TextUtils.isEmpty(this.date)) {
            return remote.getBeforeDailies(this.date)
                    .doOnSuccess(new Consumer<MainDailies.Bean>() {
                        @Override
                        public void accept(@NonNull MainDailies.Bean bean) throws Exception {
                            Repository.this.date = bean.getDate();
                            Repository.this.storyList.addAll(bean.getStoryList());
                        }
                    });
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Observable<List<ThemeDailies>> getThemeDailies(final int id) {
        themeDailies.clear();
        List<ThemeDailies> list = map.get(id);
        if (list != null && !list.isEmpty()) {
            themeDailies.addAll(list);
            return Observable.just(themeDailies);
        }
        if (MyApplication.getInstance().getNetworkState()) {
            return remote.getThemeDailies(id)
                    .map(new Function<List<ThemeDailies>, List<ThemeDailies>>() {
                        @Override
                        public List<ThemeDailies> apply(@NonNull List<ThemeDailies> themeDailies) throws Exception {
                            Repository.this.map.put(id, themeDailies);
                            Repository.this.themeDailies.addAll(themeDailies);
                            return Repository.this.themeDailies;
                        }
                    });
        } else {
            return Observable.error(new RuntimeException());
        }
    }

    @Override
    public Single<DailyExtra> getExtra(int id) {
        if (extra != null) {
            return Single.just(extra);
        }
        return remote.getExtra(id)
                .doOnSuccess(new Consumer<DailyExtra>() {
                    @Override
                    public void accept(@NonNull DailyExtra dailyExtra) throws Exception {
                        extra = dailyExtra;
                    }
                });
    }

    @Override
    public Single<DailyContent> getContent(int id, final boolean type) {
        return local.getContent(id, type)
                .onErrorResumeNext(remote.getContent(id, type).doOnSuccess(new Consumer<DailyContent>() {
                    @Override
                    public void accept(@NonNull DailyContent content) throws Exception {
                        if (type) {
                            local.saveContent(content);
                        }
                    }
                }));
    }

    @Override
    public Single<List<DailyComment.Comment>> getLongComment(int id, String longComments, String shortComments) {
        if (commentData != null && !commentData.isEmpty()) {
            return Single.just(commentData);
        } else {
            return remote.getLongComment(id, longComments, shortComments)
                    .map(new Function<List<DailyComment.Comment>, List<DailyComment.Comment>>() {
                        @Override
                        public List<DailyComment.Comment> apply(@NonNull List<DailyComment.Comment> comments) throws Exception {
                            commentData.addAll(comments);
                            return commentData;
                        }
                    });
        }
    }

    @Override
    public Single<List<DailyComment.Comment>> getShortComment(int id) {
        return remote.getShortComment(id)
                .map(new Function<List<DailyComment.Comment>, List<DailyComment.Comment>>() {
                    @Override
                    public List<DailyComment.Comment> apply(@NonNull List<DailyComment.Comment> comments) throws Exception {
                        commentData.addAll(comments);
                        return commentData;
                    }
                });
    }

    @Override
    public Single<String> getWelcomeImageURL() {
        return remote.getWelcomeImageURL()
                .doOnSuccess(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        local.saveWelcomeImageURL(s);
                    }
                })
                .onErrorResumeNext(local.getWelcomeImageURL());
    }

    public List<SkidMenu.Info> getSkidMenuData() {
        return skidMenuData;
    }

    public List<MainDailies.TopStory> getTopStoryList() {
        return topStoryList;
    }

    public List<MainDailies.Story> getStoryList() {
        return storyList;
    }

    public List<ThemeDailies> getThemeDailies() {
        return themeDailies;
    }

    public List<DailyComment.Comment> getCommentData() {
        return commentData;
    }

    public void clearMainData() {
        topStoryList.clear();
        storyList.clear();
    }

    public void clearCommentData() {
        commentData.clear();
    }

    public void clearExtra() {
        extra = null;
    }
}
