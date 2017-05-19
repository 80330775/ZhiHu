package com.qinjunyuan.zhihu.data.DataBean;


public class DailyContent {
    private String body;
    private String title;
    private String image;
    private String image_source;
    private int id;

    public DailyContent() {
    }

    public DailyContent(String body, String image, String image_source, String title) {
        this.body = body;
        this.image = image;
        this.image_source = image_source;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_source() {
        return image_source;
    }

    public int getId() {
        return id;
    }
}
