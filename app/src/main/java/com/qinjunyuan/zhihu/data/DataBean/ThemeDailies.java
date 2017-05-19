package com.qinjunyuan.zhihu.data.DataBean;

import java.util.List;


public class ThemeDailies {
    private List<ThemeDailies.Item> stories;
    private List<ThemeDailies.Editors> editors;
    private String description;
    private String background;
    private ThemeDailies.Item item;

    public ThemeDailies(String background, String description) {
        this.background = background;
        this.description = description;
    }

    public ThemeDailies(List<Editors> editors) {
        this.editors = editors;
    }

    public ThemeDailies(Item item) {
        this.item = item;
    }



    public String getBackground() {
        return background;
    }

    public String getDescription() {
        return description;
    }

    public List<Editors> getEditors() {
        return editors;
    }

    public List<Item> getStories() {
        return stories;
    }

    public Item getItem() {
        return item;
    }

    public static class Item {
        private List<String> images;
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public List<String> getImages() {
            return images;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class Editors {
        private String avatar;
        private String url;
        private String bio;
        private String name;

        public String getAvatar() {
            return avatar;
        }

        public String getBio() {
            return bio;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

//    public static class Bean {
//        private List<ThemeDailies.Editors> editors;
//        private ThemeDailies.Item item;
//        private String description;
//        private String background;
//        private String image_source;
//
//        public Bean(List<Editors> editors) {
//            this.editors = editors;
//        }
//
//        public Bean(Item item) {
//            this.item = item;
//        }
//
//        public List<Editors> getEditors() {
//            return editors;
//        }
//
//        public Item getItem() {
//            return item;
//        }
//    }
}
