package com.qinjunyuan.zhihu.comment;

import com.qinjunyuan.zhihu.BasePresenter;
import com.qinjunyuan.zhihu.BaseView;
import com.qinjunyuan.zhihu.data.DataBean.DailyComment;

import java.util.List;

public interface CommentContract {
    interface View extends BaseView<Presenter> {
        void upDateToolBar(String commentCount);

        void initRecyclerView(List<DailyComment.Comment> dataList);

        void notifyDataSetChanged();

        void scrollToTop();
    }

    interface Presenter extends BasePresenter {
        void initRecyclerView();

        void getShortComments();

        void onDestroy();
    }
}
