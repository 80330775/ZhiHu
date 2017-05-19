package com.qinjunyuan.zhihu.data.DataBean;

import java.util.List;

public class DailyComment {
    private List<DailyComment.Comment> comments;

    public List<DailyComment.Comment> getComments() {
        return comments;
    }

    public static class Comment {
        private String author, content, avatar, likes, time;
        private String commentCount;
        private DailyComment.Comment.Reply reply_to;

        public Comment() {
        }

        public Comment(String commentCount) {
            this.commentCount = commentCount;
        }


        public String getCommentCount() {
            return commentCount;
        }

        public String getAuthor() {
            return author;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getContent() {
            return content;
        }

        public String getLikes() {
            return likes;
        }

        public String getTime() {
            return time;
        }

        public DailyComment.Comment.Reply getReply_to() {
            return reply_to;
        }

        public static class Reply {
            private String content, author, error_msg;
            private int status;

            public String getAuthor() {
                return author;
            }

            public String getContent() {
                return content;
            }

            public String getError_msg() {
                return error_msg;
            }

            public int getStatus() {
                return status;
            }
        }
    }
}
