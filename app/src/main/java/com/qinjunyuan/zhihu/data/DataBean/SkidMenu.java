package com.qinjunyuan.zhihu.data.DataBean;


import java.util.List;

public class SkidMenu {
    private List<SkidMenu.Info> others;

    public List<SkidMenu.Info> getOthers() {
        return others;
    }

    public static class Info {
        private String name;
        private int id;

        public Info(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}
