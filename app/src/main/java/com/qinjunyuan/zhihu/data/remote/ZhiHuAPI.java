package com.qinjunyuan.zhihu.data.remote;

import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.data.DataBean.WelcomeImage;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ZhiHuAPI {
    @GET("7/prefetch-launch-images/1080*1920")
    Single<WelcomeImage> getWelcomeImage();

    @GET("4/news/before/{date}")
    Single<MainDailies> getBeforeDailies(@Path("date") String date);

    @GET("4/news/latest")
    Single<MainDailies> getLatestDaily();

    @GET("4/themes")
    Observable<SkidMenu> getSkidMenu();

    @GET("4/theme/{id}")
    Observable<ThemeDailies> getThemeDaily(@Path("id") int id);

    @GET("4/news/{id}")
    Single<DailyContent> getContent(@Path("id") int id);

    @GET("4/news/{id}")
    Call<DailyContent> getContentCall(@Path("id") int id);

    @GET("4/story-extra/{id}")
    Single<DailyExtra> getExtra(@Path("id") int id);

    @GET("4/story/{id}/long-comments")
    Single<DailyComment> getLongComments(@Path("id") int id);

    @GET("4/story/{id}/short-comments")
    Single<DailyComment> getShortComments(@Path("id") int id);
}
