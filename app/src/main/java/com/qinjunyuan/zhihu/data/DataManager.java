package com.qinjunyuan.zhihu.data;

import com.qinjunyuan.zhihu.data.DataBean.DailyComment;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataManager {
    Single<MainDailies.Bean> getLatestDailies();

    Single<MainDailies.Bean> getBeforeDailies(String date);

    Observable<List<SkidMenu.Info>> getSkidMenu();

    Observable<List<ThemeDailies>> getThemeDailies(int id);

    Single<DailyExtra> getExtra(int id);

    Single<DailyContent> getContent(int id, boolean type);

    Single<List<DailyComment.Comment>> getLongComment(int id, String longComments, String shortComments);

    Single<List<DailyComment.Comment>> getShortComment(int id);

    Single<String> getWelcomeImageURL();
}
