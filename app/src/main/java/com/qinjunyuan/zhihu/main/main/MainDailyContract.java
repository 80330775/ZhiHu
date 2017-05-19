package com.qinjunyuan.zhihu.main.main;


import com.qinjunyuan.zhihu.BasePresenter;
import com.qinjunyuan.zhihu.BaseView;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;

import java.util.List;

public interface MainDailyContract {
    interface View extends BaseView<Presenter> {
        void initViewPager(List<MainDailies.TopStory> dataList);

        void initRecyclerView(List<MainDailies.Story> dataList);

        void notifyDataSetChanged();

        void notifyItemRangeInserted(int upDateCount);

        void setRefreshing(boolean refreshing);
    }

    interface Presenter extends BasePresenter {
        void initRecyclerView();

        void getLatestDailyList();

        void getBeforeDailies();

        void onRefresh();
    }
}
