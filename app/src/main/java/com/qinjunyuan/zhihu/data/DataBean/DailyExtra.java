package com.qinjunyuan.zhihu.data.DataBean;


import java.io.Serializable;

public class DailyExtra implements Serializable{
    private String long_comments, short_comments, comments;
    private int popularity;

    public String getComments() {
        return comments;
    }

    public String getLong_comments() {
        return long_comments;
    }

    public int getPopularity() {
        return popularity;
    }

    public String getShort_comments() {
        return short_comments;
    }
}
