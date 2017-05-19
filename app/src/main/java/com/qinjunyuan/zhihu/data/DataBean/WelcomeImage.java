package com.qinjunyuan.zhihu.data.DataBean;


import java.util.List;

public class WelcomeImage {
    private List<URL> creatives;

    public List<URL> getCreatives() {
        return creatives;
    }

    public static class URL {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
