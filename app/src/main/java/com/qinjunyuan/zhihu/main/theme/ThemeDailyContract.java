package com.qinjunyuan.zhihu.main.theme;


import com.qinjunyuan.zhihu.BasePresenter;
import com.qinjunyuan.zhihu.BaseView;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;

import java.util.List;

interface ThemeDailyContract {
    interface View extends BaseView<Presenter> {
        void start();

        void initRecyclerView(List<ThemeDailies> list);

        void notifyDataSetChanged();
    }

    interface Presenter extends BasePresenter {
        void initRecyclerView();

        void setId(int id);
    }
}
