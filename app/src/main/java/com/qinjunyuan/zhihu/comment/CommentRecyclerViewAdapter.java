package com.qinjunyuan.zhihu.comment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.view.RxView;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.DailyComment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;

class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private boolean isDay;
    private List<DailyComment.Comment> dataList;
    private Context context;
    private static final int TYPE_NO_LONG_COMMENTS = 0, TYPE_NORMAL = 1, TYPE_LONG = 2, TYPE_SHORT = 3, TYPE_REPLY = 4, TYPE_ERROR = 5;

    private OnShortCommentsHeaderClickListener listener;


    interface OnShortCommentsHeaderClickListener {
        void shortCommentsViewClick(Observable<Void> observable);
    }

    void setOnShortCommentsHeaderClickListener(OnShortCommentsHeaderClickListener listener) {
        this.listener = listener;
    }

    CommentRecyclerViewAdapter(List<DailyComment.Comment> dataList, boolean isDay) {
        this.dataList = dataList;
        this.isDay = isDay;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(dataList.get(position).getCommentCount())) {
            if (position == 0) {
                return TYPE_LONG;
            } else {
                return TYPE_SHORT;
            }
        }
        if (position == 1 && dataList.get(1).getContent() == null) {
            return TYPE_NO_LONG_COMMENTS;
        }
        if (dataList.get(position).getReply_to() != null) {
            DailyComment.Comment.Reply reply = dataList.get(position).getReply_to();
            if (reply.getStatus() != 0 && !TextUtils.isEmpty(reply.getError_msg())) {
                return TYPE_ERROR;
            } else {
                return TYPE_REPLY;
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (viewType == TYPE_LONG || viewType == TYPE_SHORT) {
            View view = LayoutInflater.from(context).inflate(R.layout.comments_list_header, parent, false);
            return new ViewHolder(view, TYPE_LONG);
        }
        if (viewType == TYPE_NO_LONG_COMMENTS) {
            View view = LayoutInflater.from(context).inflate(R.layout.comment_list_nothing, parent, false);
            return new ViewHolder(view, TYPE_NO_LONG_COMMENTS);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false);
        return new ViewHolder(view, TYPE_NORMAL);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            StringBuilder builder = new StringBuilder();
            Resources res = context.getResources();
            builder.append(dataList.get(0).getCommentCount()).append(" ").append(res.getString(R.string.long_comments));
            holder.header.setText(builder);
        }
        if (getItemViewType(position) == TYPE_SHORT) {
            StringBuilder builder = new StringBuilder();
            Resources res = context.getResources();
            builder.append(dataList.get(position).getCommentCount()).append(" ").append(res.getString(R.string.short_comments));
            holder.header.setText(builder);
            listener.shortCommentsViewClick(RxView.clicks(holder.header));
        }
        if (getItemViewType(position) == TYPE_NORMAL) {
            setData(holder, position);
            holder.reply.setHeight(0);
        }
        if (getItemViewType(position) == TYPE_REPLY) {
            setData(holder, position);
            DailyComment.Comment.Reply reply = dataList.get(position).getReply_to();
            String author = reply.getAuthor();
            String content = reply.getContent();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("//").append(author).append("：").append(content);//////////
            builder.setSpan(new StyleSpan(Typeface.BOLD), 0, author.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (isDay) {
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.black)), 0, author.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_4)), 0, author.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            holder.reply.setText(builder);
        }
        if (getItemViewType(position) == TYPE_ERROR) {
            setData(holder, position);
            DailyComment.Comment.Reply reply = dataList.get(position).getReply_to();
            if (reply.getStatus() != 0 && !TextUtils.isEmpty(reply.getError_msg())) {
                holder.reply.setText(reply.getError_msg());
                if (isDay) {
                    holder.reply.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_1));
                } else {
                    holder.reply.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_13));
                }
            }
        }
    }

    private void setData(ViewHolder holder, int position) {
        DailyComment.Comment itemData = dataList.get(position);
        Glide.with(context).load(itemData.getAvatar()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.avatar);
        holder.author.setText(itemData.getAuthor());
        holder.likes.setText(itemData.getLikes());
        holder.content.setText(itemData.getContent());
        String time = itemData.getTime();
        long t = Long.parseLong(time);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        String result = format.format(t * 1000);//因为返回的时间是10位数的，正常是13位数，所以剩余1000
        holder.time.setText(result);
    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()) {
            return 0;
        }
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatar;
        private TextView author, likes, content, time, reply;
        private TextView header;

        ViewHolder(View itemView, int type) {
            super(itemView);
            if (type == TYPE_NO_LONG_COMMENTS) {
                return;
            }
            if (type == TYPE_LONG) {
                header = (TextView) itemView.findViewById(R.id.comments_header);
            }
            if (type == TYPE_NORMAL) {
                avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
                author = (TextView) itemView.findViewById(R.id.author);
                likes = (TextView) itemView.findViewById(R.id.likes);
                content = (TextView) itemView.findViewById(R.id.content);
                time = (TextView) itemView.findViewById(R.id.time);
                reply = (TextView) itemView.findViewById(R.id.reply);
            }
        }
    }
}
