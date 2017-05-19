package com.qinjunyuan.zhihu.data.remote;

import android.content.Context;
import android.util.Log;

import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.data.DataBean.WelcomeImage;
import com.qinjunyuan.zhihu.data.DataManager;
import com.qinjunyuan.zhihu.util.MyApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class RemoteDataSource implements DataManager {
    private ZhiHuAPI api;

    @Inject
    RemoteDataSource(ZhiHuAPI api) {
        this.api = api;
    }

    @Override
    public Single<MainDailies.Bean> getLatestDailies() {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getLatestDaily()
                    .subscribeOn(Schedulers.io())
                    .map(new Function<MainDailies, MainDailies.Bean>() {
                        @Override
                        public MainDailies.Bean apply(@NonNull MainDailies mainDailies) throws Exception {
                            List<MainDailies.Story> storyList = new ArrayList<>();
                            MainDailies.Story date = new MainDailies.Story();
                            date.setDate("今日热闻");
                            date.setHeader(true);
                            storyList.add(date);
                            for (MainDailies.Story story : mainDailies.getStories()) {
                                MainDailies.Story data = new MainDailies.Story();
                                data.setId(story.getId());
                                data.setImage(story.getImages().get(0));
                                data.setTitle(story.getTitle());
                                data.setDate("今日热闻");
                                storyList.add(data);
                            }
                            List<MainDailies.TopStory> topStoryList = mainDailies.getTop_stories();
                            MainDailies.Bean bean = new MainDailies.Bean();
                            bean.setDate(mainDailies.getDate());
                            bean.setStoryList(storyList);
                            bean.setTopStoryList(topStoryList);
                            return bean;
                        }
                    });

        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<MainDailies.Bean> getBeforeDailies(String date) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getBeforeDailies(date)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<MainDailies, MainDailies.Bean>() {
                        @Override
                        public MainDailies.Bean apply(@NonNull MainDailies mainDailies) throws Exception {
                            List<MainDailies.Story> dataList = new ArrayList<>();
                            MainDailies.Story date = new MainDailies.Story();
                            String s = parseDate(mainDailies.getDate());
                            date.setDate(s);
                            date.setHeader(true);
                            dataList.add(date);
                            for (MainDailies.Story story : mainDailies.getStories()) {
                                MainDailies.Story data = new MainDailies.Story();
                                data.setId(story.getId());
                                data.setImage(story.getImages().get(0));
                                data.setTitle(story.getTitle());
                                data.setDate(s);
                                dataList.add(data);
                            }
                            MainDailies.Bean bean = new MainDailies.Bean();
                            bean.setDate(mainDailies.getDate());
                            bean.setStoryList(dataList);
                            bean.setUpDateCount(dataList.size());
                            return bean;
                        }
                    });
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Observable<List<SkidMenu.Info>> getSkidMenu() {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getSkidMenu()
                    .subscribeOn(Schedulers.io())
                    .map(new Function<SkidMenu, List<SkidMenu.Info>>() {
                        @Override
                        public List<SkidMenu.Info> apply(@NonNull SkidMenu skidMenu) throws Exception {
                            return skidMenu.getOthers();
                        }
                    });
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Observable<List<ThemeDailies>> getThemeDailies(int id) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getThemeDaily(id)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<ThemeDailies, List<ThemeDailies>>() {
                        @Override
                        public List<ThemeDailies> apply(@NonNull ThemeDailies list) throws Exception {
                            List<ThemeDailies> mList = new ArrayList<>();
                            mList.add(new ThemeDailies(list.getBackground(), list.getDescription()));
                            mList.add(new ThemeDailies(list.getEditors()));
                            for (ThemeDailies.Item item : list.getStories()) {
                                mList.add(new ThemeDailies(item));
                            }
                            return mList;
                        }
                    });
        } else {
            return Observable.empty();
        }
    }

    @Override
    public Single<DailyExtra> getExtra(int id) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getExtra(id)
                    .subscribeOn(Schedulers.io());
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<DailyContent> getContent(int id, boolean type) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getContent(id)
                    .subscribeOn(Schedulers.io());
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<List<DailyComment.Comment>> getLongComment(int id, final String longComments, final String shortComments) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getLongComments(id)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<DailyComment, List<DailyComment.Comment>>() {
                        @Override
                        public List<DailyComment.Comment> apply(@NonNull DailyComment dailyComment) throws Exception {
                            List<DailyComment.Comment> list = dailyComment.getComments();
                            if (list.isEmpty()) {
                                list.add(new DailyComment.Comment(longComments));
                                list.add(new DailyComment.Comment());
                                list.add(new DailyComment.Comment(shortComments));
                            } else {
                                list.add(0, new DailyComment.Comment(longComments));
                                list.add(new DailyComment.Comment(shortComments));
                            }
                            return list;
                        }
                    });
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<List<DailyComment.Comment>> getShortComment(int id) {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getShortComments(id)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<DailyComment, List<DailyComment.Comment>>() {
                        @Override
                        public List<DailyComment.Comment> apply(@NonNull DailyComment daily_comment) throws Exception {
                            return daily_comment.getComments();
                        }
                    });
        } else {
            return Single.error(new RuntimeException());
        }
    }

    @Override
    public Single<String> getWelcomeImageURL() {
        if (MyApplication.getInstance().getNetworkState()) {
            return api.getWelcomeImage()
                    .subscribeOn(Schedulers.io())
                    .map(new Function<WelcomeImage, String>() {
                        @Override
                        public String apply(@NonNull WelcomeImage welcomeImage) throws Exception {
                            return welcomeImage.getCreatives().get(0).getUrl();
                        }
                    });
        } else {
            return Single.error(new RuntimeException());
        }
    }

    public Single<MainDailies> downloadLatestDaily1() {
        return api.getLatestDaily().subscribeOn(Schedulers.io());
    }

    public Observable<MainDailies.CompleteStory> downloadLatestDaily2(MainDailies mainDailies) {
        List<MainDailies.CompleteStory> list = new ArrayList<>();
        List<MainDailies.TopStory> list1 = mainDailies.getTop_stories();
        Collections.reverse(list1);
        List<MainDailies.Story> list2 = mainDailies.getStories();
        Collections.reverse(list2);
        list.addAll(list1);
        list.addAll(list2);

        Observable<MainDailies.CompleteStory> o = Observable.fromIterable(list);
        Observable<String> date = Observable.just(mainDailies.getDate()).repeat(list.size());
        Observable<String> percent = Observable.zip(Observable.range(1, list.size()), Observable.just(list.size()).repeat(list.size()), new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                return integer * 100 / integer2 + "%";
            }
        });
        Observable<DailyContent> content = Observable.fromIterable(list)
                .map(new Function<MainDailies.CompleteStory, Integer>() {
                    @Override
                    public Integer apply(@NonNull MainDailies.CompleteStory o) throws Exception {
                        return o.getId();
                    }
                })
                .map(new Function<Integer, DailyContent>() {
                    @Override
                    public DailyContent apply(@NonNull Integer integer) throws Exception {
                        return api.getContentCall(integer).execute().body();
                    }
                });

        return Observable.zip(o, date, percent, content, new Function4<MainDailies.CompleteStory, String, String, DailyContent, MainDailies.CompleteStory>() {
            @Override
            public MainDailies.CompleteStory apply(@NonNull MainDailies.CompleteStory o,
                                                   @NonNull String s, @NonNull String s2, @NonNull DailyContent dailyContent) throws Exception {
                MainDailies.CompleteStory completeStory = new MainDailies.CompleteStory();
                String image = null;
                if (o instanceof MainDailies.TopStory) {
                    completeStory.setType("topStory");
                    MainDailies.TopStory topStory = (MainDailies.TopStory) o;
                    image = topStory.getImage();
                }
                if (o instanceof MainDailies.Story) {
                    completeStory.setType("story");
                    MainDailies.Story story = (MainDailies.Story) o;
                    image = story.getImages().get(0);
                }
                completeStory.setId(o.getId());
                completeStory.setImage(image);
                completeStory.setTitle(o.getTitle());
                completeStory.setContent(dailyContent);
                completeStory.setDate(s);
                completeStory.setPercent(s2);
                return completeStory;
            }
        });
    }

    private String parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        String week = "星期";
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            week += "天";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            week += "一";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            week += "二";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            week += "三";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            week += "四";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            week += "五";
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            week += "六";
        }
        return month + "月" + day + "日" + " " + week;
    }
}

