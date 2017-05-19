package com.qinjunyuan.zhihu.content;

import com.qinjunyuan.zhihu.BasePresenter;
import com.qinjunyuan.zhihu.BaseView;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;

public interface ContentContract {
    interface View extends BaseView<Presenter> {
        void upDateToolBar(DailyExtra extra);

        void upDateWebView(DailyContent content);
    }

    interface Presenter extends BasePresenter {
        void onDestroy();
    }
}
