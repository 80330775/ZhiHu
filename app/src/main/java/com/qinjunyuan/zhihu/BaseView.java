package com.qinjunyuan.zhihu;


public interface BaseView<T> {
    void setPresenter(T presenter);

    boolean isActive();

    void makeToast();
}
