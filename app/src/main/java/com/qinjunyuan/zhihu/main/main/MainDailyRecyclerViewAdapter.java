package com.qinjunyuan.zhihu.main.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.content.ContentActivity;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;

import java.util.List;


class MainDailyRecyclerViewAdapter extends RecyclerView.Adapter<MainDailyRecyclerViewAdapter.ViewHolder> {
    private List<MainDailies.Story> dataList;
    private Context context;
    private LayoutInflater inflater;
    private View headerView;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_UPDATE = 2;
    private int dateColor, textColor, backgroundColor;

    void setThemeMode(boolean isDay) {
        if (isDay) {
            dateColor = ContextCompat.getColor(context, R.color.grey_11);
            textColor = ContextCompat.getColor(context, R.color.black);
            backgroundColor = ContextCompat.getColor(context, R.color.white);
        } else {
            dateColor = ContextCompat.getColor(context, R.color.grey_3);
            textColor = ContextCompat.getColor(context, R.color.grey_1);
            backgroundColor = ContextCompat.getColor(context, R.color.grey_11);
        }
        notifyDataSetChanged();
    }

    MainDailyRecyclerViewAdapter(List<MainDailies.Story> dataList) {
        this.dataList = dataList;
    }

    void setHeaderView(View view) {
        headerView = view;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        }
        if (dataList.get(position - 1).isHeader()) {
            return TYPE_UPDATE;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        if (viewType == TYPE_HEADER) {
            return new ViewHolder(headerView, TYPE_HEADER);
        }
        if (viewType == TYPE_UPDATE) {
            View view = inflater.inflate(R.layout.main_list_date, parent, false);
            return new ViewHolder(view, TYPE_UPDATE);
        } else {
            View view = inflater.inflate(R.layout.main_list_item, parent, false);
            return new ViewHolder(view, TYPE_NORMAL);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            return;
        }
        final MainDailies.Story itemData = dataList.get(position - 1);
        if (type == TYPE_UPDATE) {
            holder.textView_date.setText(itemData.getDate());
            if (dateColor != 0) {
                holder.textView_date.setTextColor(dateColor);
            }
        }
        if (type == TYPE_NORMAL) {
            holder.textView.setText(itemData.getTitle());
            if (textColor != 0) {
                holder.textView.setTextColor(textColor);
            }
            if (backgroundColor != 0) {
                holder.cardView.setCardBackgroundColor(backgroundColor);
            }
            Glide.with(context)
                    .load(itemData.getImage())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra("id", itemData.getId());
                    intent.putExtra("type", true);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (headerView == null) {
            return 0;
        }
        return dataList.size() + 1;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView_date;
        ImageView imageView;
        CardView cardView;

        ViewHolder(View itemView, int type) {
            super(itemView);
            if (type == TYPE_HEADER) {
                return;
            }
            if (type == TYPE_UPDATE) {
                textView_date = (TextView) itemView.findViewById(R.id.date);
                return;
            }
            if (type == TYPE_NORMAL) {
                textView = (TextView) itemView.findViewById(R.id.textView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
            }
        }
    }

    String getDate(int position) {
        if (dataList != null && !dataList.isEmpty()) {
            String date = dataList.get(position).getDate();
            if (!TextUtils.isEmpty(date)) {
                return date;
            }
        }
        return null;
    }
}
