package com.qinjunyuan.zhihu.main;

import android.content.Context;

import com.qinjunyuan.zhihu.BasePresenter;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;

import java.util.List;

interface MainActivityContract {
    interface View {
        void initRecyclerView(List<SkidMenu.Info> dataList);

        void notifyDataSetChanged();

        void makeToast();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void initRecyclerView();

        void download(Context context);

        void onDestroy();
    }
}
