package com.qinjunyuan.zhihu.data.local;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.data.DataBean.WelcomeImage;
import com.qinjunyuan.zhihu.data.DataManager;
import com.qinjunyuan.zhihu.util.MyApplication;
import com.qinjunyuan.zhihu.util.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class LocalDataSource implements DataManager {
    private SQLiteDatabase db;
    private SharedPreferences sp;

    @Inject
    LocalDataSource(SQLiteDatabase db, SharedPreferences sp) {
        this.db = db;
        this.sp = sp;
    }

    public Disposable saveLatestDailyList(final MainDailies.Bean bean) {
        List<MainDailies.TopStory> mViewPagerData = new ArrayList<>();
        mViewPagerData.addAll(bean.getTopStoryList());
        Collections.reverse(mViewPagerData);

        List<MainDailies.Story> mRecyclerViewData = new ArrayList<>();
        mRecyclerViewData.addAll(bean.getStoryList());
        Collections.reverse(mRecyclerViewData);

        Observable<Object> o1 = Observable.fromIterable(mViewPagerData)
                .filter(new Predicate<MainDailies.TopStory>() {
                    @Override
                    public boolean test(@NonNull MainDailies.TopStory topStory) throws Exception {
                        return !db.rawQuery("select mId from mainPage where mId = " + topStory.getId() + " and type = 'topStory'", null).moveToFirst();
                    }
                })
                .cast(Object.class);
        Observable<Object> o2 = Observable.fromIterable(mRecyclerViewData)
                .filter(new Predicate<MainDailies.Story>() {
                    @Override
                    public boolean test(@NonNull MainDailies.Story story) throws Exception {
                        return !db.rawQuery("select mId from mainPage where mId = " + story.getId() + " and type = 'story'", null).moveToFirst();
                    }
                })
                .cast(Object.class);

        int topStoryCount = db.rawQuery("select type from mainPage where type = 'topStory'", null).getCount();//可以删
        Log.d("tjy", "数据库现在有 " + topStoryCount + " 条topStory数据");
        int storyCount = db.rawQuery("select type from mainPage where type = 'story'", null).getCount();//可以删
        Log.d("tjy", "数据库现在有 " + storyCount + " 条story数据");

        final ContentValues values = new ContentValues();
        return Observable.concat(o1, o2)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new ResourceObserver<Object>() {
                    @Override
                    public void onNext(Object data) {
                        if (data instanceof MainDailies.TopStory) {
                            MainDailies.TopStory topStory = (MainDailies.TopStory) data;
                            int id = topStory.getId();
                            String image = topStory.getImage();
                            String title = topStory.getTitle();
                            values.put("mId", id);
                            values.put("itemImage", image);
                            values.put("itemTitle", title);
                            values.put("time", new Date().getTime());
                            values.put("date", bean.getDate());
                            values.put("type", "topStory");
                            db.insert("mainPage", null, values);
                            values.clear();
                            Log.d("tjy", "往database插入了一条数据 ");
                            return;
                        }
                        if (data instanceof MainDailies.Story) {
                            MainDailies.Story story = (MainDailies.Story) data;
                            if (story.getDate() == null) {
                                int id = story.getId();
                                String image = story.getImage();
                                String title = story.getTitle();
                                values.put("mId", id);
                                values.put("itemImage", image);
                                values.put("itemTitle", title);
                                values.put("time", new Date().getTime());
                                values.put("date", bean.getDate());
                                values.put("type", "story");
                                db.insert("mainPage", null, values);
                                values.clear();
                                Log.d("tjy", "往database插入了一条数据 ");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        db.execSQL("delete from mainPage where (select count(type) from mainPage where type = 'topStory') > " + bean.getTopStoryList().size() +
                                " and time in (select time from mainPage where type = 'topStory' order by time desc limit " + bean.getTopStoryList().size() + ", -1)");

                        db.execSQL("delete from mainPage where (select count(type) from mainPage where type = 'story') > " + bean.getStoryList().size() +
                                " and time in (select time from mainPage where type = 'story' order by time desc limit " + bean.getStoryList().size() + ", -1)");

                        int topStoryCount = db.rawQuery("select type from mainPage where type = 'topStory'", null).getCount();//可以删
                        Log.d("tjy", "数据库现在有 " + topStoryCount + " 条topStory数据");

                        int storyCount = db.rawQuery("select type from mainPage where type = 'story'", null).getCount();//可以删
                        Log.d("tjy", "数据库现在有 " + storyCount + " 条story数据");
                    }
                });
    }

    @Override
    public Single<MainDailies.Bean> getLatestDailies() {
        Cursor cursor;
        cursor = db.rawQuery("select mId from mainPage", null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return Single.error(new RuntimeException());
        }
        cursor.close();
        MainDailies.Bean bean = new MainDailies.Bean();
        cursor = db.rawQuery("select mId, itemImage, itemTitle from mainPage where type = 'topStory' order by time desc", null);
        if (cursor.moveToFirst()) {
            List<MainDailies.TopStory> topStoryList = new ArrayList<>();
            do {
                MainDailies.TopStory topStory = new MainDailies.TopStory();
                topStory.setId(cursor.getInt(cursor.getColumnIndex("mId")));
                topStory.setImage(cursor.getString(cursor.getColumnIndex("itemImage")));
                topStory.setTitle(cursor.getString(cursor.getColumnIndex("itemTitle")));
                topStoryList.add(topStory);
            } while (cursor.moveToNext());
            bean.setTopStoryList(topStoryList);
        }
        cursor.close();
        cursor = db.rawQuery("select date, mId, itemImage, itemTitle from mainPage where type = 'story' order by time desc", null);
        if (cursor.moveToFirst()) {
            bean.setDate(cursor.getString(cursor.getColumnIndex("date")));
            List<MainDailies.Story> storyList = new ArrayList<>();
            MainDailies.Story date = new MainDailies.Story();
            date.setDate("今日热闻");
            storyList.add(date);
            do {
                MainDailies.Story story = new MainDailies.Story();
                story.setId(cursor.getInt(cursor.getColumnIndex("mId")));
                story.setImage(cursor.getString(cursor.getColumnIndex("itemImage")));
                story.setTitle(cursor.getString(cursor.getColumnIndex("itemTitle")));
                storyList.add(story);
            } while (cursor.moveToNext());
            bean.setStoryList(storyList);
        }
        cursor.close();
        return Single.just(bean);
    }

    public void saveContent(DailyContent content) {
//        Log.d("tjy", "保存Content到数据库");
        ContentValues values = new ContentValues();
        values.put("contentTitle", content.getTitle());
        values.put("contentImage", content.getImage());
        values.put("contentSource", content.getImage_source());
        values.put("content", content.getBody());
        db.update("mainPage", values, "mId = " + content.getId(), null);
//        Cursor cursor = db.rawQuery("select contentTitle, contentImage, contentSource, content from mainPage where mId = " + content.getId(), null);
//        if (cursor.moveToFirst()) {
//            Log.d("tjy", cursor.getString(cursor.getColumnIndex("contentTitle")));
//            Log.d("tjy", cursor.getString(cursor.getColumnIndex("contentImage")));
//            Log.d("tjy", cursor.getString(cursor.getColumnIndex("contentSource")));
//        }
//        cursor.close();
    }

    @Override
    public Single<DailyContent> getContent(int id, boolean type) {
        return Single.just(id)
                .map(new Function<Integer, Cursor>() {
                    @Override
                    public Cursor apply(@NonNull Integer integer) throws Exception {
                        return db.rawQuery("select contentTitle, contentImage, contentSource, content from mainPage where mId = " + integer, null);
                    }
                })
                .filter(new Predicate<Cursor>() {
                    @Override
                    public boolean test(@NonNull Cursor cursor) throws Exception {
                        return cursor.moveToFirst() && !TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("content")));
                    }
                })
                .map(new Function<Cursor, DailyContent>() {
                    @Override
                    public DailyContent apply(@NonNull Cursor cursor) throws Exception {
                        String body = cursor.getString(cursor.getColumnIndex("content"));
                        String image = cursor.getString(cursor.getColumnIndex("contentImage"));
                        String source = cursor.getString(cursor.getColumnIndex("contentSource"));
                        String title = cursor.getString(cursor.getColumnIndex("contentTitle"));
                        cursor.close();
                        Log.d("tjy", "从数据库找到了Content ");
                        return new DailyContent(body, image, source, title);
                    }
                })
                .toSingle();
    }

    public void saveSkidMenu(List<SkidMenu.Info> list) {
        final ContentValues values = new ContentValues();
        Observable.fromIterable(list)
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<SkidMenu.Info>() {
                    @Override
                    public boolean test(@NonNull SkidMenu.Info info) throws Exception {
                        int id = info.getId();
                        return !db.rawQuery("select * from skidMenu where mId = " + id, null).moveToFirst();
                    }
                })
                .subscribe(new Consumer<SkidMenu.Info>() {
                    @Override
                    public void accept(@NonNull SkidMenu.Info info) throws Exception {
                        values.put("mId", info.getId());
                        values.put("name", info.getName());
                        db.insert("skidMenu", null, values);
                        values.clear();
                    }
                });
    }

    @Override
    public Observable<List<SkidMenu.Info>> getSkidMenu() {
        Cursor cursor = db.rawQuery("select * from skidMenu", null);
        List<SkidMenu.Info> dataList = null;
        if (cursor.moveToFirst()) {
            dataList = new ArrayList<>();
            do {
                int id = cursor.getInt(cursor.getColumnIndex("mId"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                dataList.add(new SkidMenu.Info(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (dataList != null && !dataList.isEmpty()) {
            return Observable.just(dataList);
        } else {
            return Observable.empty();
        }
    }

    public void saveWelcomeImageURL(String s) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("welcomeURL", s);
        editor.apply();
    }

    @Override
    public Single<String> getWelcomeImageURL() {
        String s = sp.getString("welcomeURL", "");
        if (!TextUtils.isEmpty(s)) {
            return Single.just(s);
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<MainDailies.Bean> getBeforeDailies(String date) {
        return null;
    }

    @Override
    public Observable<List<ThemeDailies>> getThemeDailies(int id) {
        return null;
    }

    @Override
    public Single<DailyExtra> getExtra(int id) {
        return null;
    }

    @Override
    public Single<List<DailyComment.Comment>> getLongComment(int id, String longComments, String shortComments) {
        return null;
    }

    @Override
    public Single<List<DailyComment.Comment>> getShortComment(int id) {
        return null;
    }


    public void saveOfflineDownload(Observable<MainDailies.CompleteStory> observable, final Context context) {
        db.delete("mainPage", null, null);
        observable
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.d("tjy", "清除内存缓存的操作在" + Thread.currentThread().getName() + "线程中");
                        Glide.get(MyApplication.getInstance()).clearMemory();//清除内存缓存要在main thread
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.d("tjy", "清除硬盘缓存的操作在" + Thread.currentThread().getName() + "线程中");
                        Glide.get(MyApplication.getInstance()).clearDiskCache();//清除硬盘缓存要在worker thread
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<MainDailies.CompleteStory>() {
                    ContentValues values;
                    int large_width, large_height, small_width, small_height, content_width, content_height;
                    NotificationManager manager;

                    @Override
                    public void onSubscribe(Disposable d) {
                        values = new ContentValues();
                        SharedPreferences sp = context.getSharedPreferences("size", 0);
                        large_width = sp.getInt("large_width", 0);
                        large_height = sp.getInt("large_height", 0);
                        small_width = sp.getInt("small_width", 0);
                        small_height = sp.getInt("small_height", 0);
                        content_width = sp.getInt("content_width", 0);
                        content_height = sp.getInt("content_height", 0);

                        manager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new NotificationCompat.Builder(MyApplication.getInstance())
                                .setContentTitle("离线下载")
                                .setContentText("正在离线下载最新内容")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .build();
                        manager.notify(0, notification);
                    }

                    @Override
                    public void onNext(MainDailies.CompleteStory story) {
                        if (story.getType().equals("topStory")) {
                            values.put("type", "topStory");
                            if (large_width != 0 && large_height != 0) {
                                Glide.with(context).load(story.getImage()).downloadOnly(large_width, large_height);
                                Log.d("tjy", "Glide下载large图片 ");
                            }
                        }
                        if (story.getType().equals("story")) {
                            values.put("type", "story");
                            if (small_width != 0 && small_height != 0) {
                                Glide.with(context).load(story.getImage()).downloadOnly(small_width, small_height);
                                Log.d("tjy", "Glide下载small图片 ");
                            }
                        }
                        values.put("date", story.getDate());
                        values.put("mId", story.getId());
                        values.put("itemImage", story.getImage());
                        values.put("itemTitle", story.getTitle());
                        values.put("time", new Date().getTime());
                        DailyContent content = story.getContent();
                        values.put("contentTitle", content.getTitle());
                        values.put("contentImage", content.getImage());
                        values.put("contentSource", content.getImage_source());
                        values.put("content", content.getBody());
                        if (content_width != 0 && content_height != 0) {
                            Glide.with(context).load(content.getImage()).downloadOnly(content_width, content_height);
                            Log.d("tjy", "Glide下载content图片 ");
                        }
                        db.insert("mainPage", null, values);
                        values.clear();
                        RxBus.getInstance().post(1, story.getPercent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        values = null;
                        Completable.create(new CompletableOnSubscribe() {
                            @Override
                            public void subscribe(CompletableEmitter e) throws Exception {
                                e.onComplete();
                            }
                        }).delay(1, TimeUnit.SECONDS)
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }
                                    @Override
                                    public void onComplete() {
                                        manager.cancel(0);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        manager.cancel(0);
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {
                        values = null;
                        Completable.create(new CompletableOnSubscribe() {
                            @Override
                            public void subscribe(CompletableEmitter e) throws Exception {
                                e.onComplete();
                            }
                        }).delay(1, TimeUnit.SECONDS)
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }
                                    @Override
                                    public void onComplete() {
                                        manager.cancel(0);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        manager.cancel(0);
                                    }
                                });
                    }
                });
    }
}


