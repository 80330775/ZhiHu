package com.qinjunyuan.zhihu.data.DataBean;

import java.util.List;

public class MainDailies {
    private String date;
    private List<MainDailies.TopStory> top_stories;
    private List<MainDailies.Story> stories;

    public String getDate() {
        return date;
    }

    public List<MainDailies.TopStory> getTop_stories() {
        return top_stories;
    }

    public List<MainDailies.Story> getStories() {
        return stories;
    }

    public static class TopStory extends CompleteStory {
    }

    public static class Story extends CompleteStory {
        private List<String> images;

        public List<String> getImages() {
            return images;
        }
    }

    public static class Bean {
        private String date;
        private List<Story> storyList;
        private List<TopStory> topStoryList;
        private int upDateCount;

        public String getDate() {
            return date;
        }

        public List<Story> getStoryList() {
            return storyList;
        }

        public List<TopStory> getTopStoryList() {
            return topStoryList;
        }

        public int getUpDateCount() {
            return upDateCount;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setStoryList(List<Story> storyList) {
            this.storyList = storyList;
        }

        public void setTopStoryList(List<TopStory> topStoryList) {
            this.topStoryList = topStoryList;
        }

        public void setUpDateCount(int upDateCount) {
            this.upDateCount = upDateCount;
        }
    }

    public static class CompleteStory {
        private int id;
        private String image, title, type, date, percent;
        private DailyContent content;
        private boolean isHeader;

        public DailyContent getContent() {
            return content;
        }

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public String getDate() {
            return date;
        }

        public String getPercent() {
            return percent;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setContent(DailyContent content) {
            this.content = content;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setPercent(String percent) {
            this.percent = percent;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }
    }
}
