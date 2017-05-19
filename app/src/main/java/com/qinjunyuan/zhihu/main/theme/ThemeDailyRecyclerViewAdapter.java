package com.qinjunyuan.zhihu.main.theme;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.content.ContentActivity;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.util.Editors;

import java.util.List;


class ThemeDailyRecyclerViewAdapter extends RecyclerView.Adapter<ThemeDailyRecyclerViewAdapter.ViewHolder> {
    private List<ThemeDailies> dataList;
    private static final int TYPE_HEADER = 0, TYPE_EDITOR = 1, TYPE_ITEM_TV = 2, TYPE_ITEM_TIV = 3;
    private Context context;
    private LayoutInflater inflater;
    private int editorTextColor, textColor, backgroundColor;

    void setThemeMode(boolean isDay) {
        if (isDay) {
            editorTextColor = ContextCompat.getColor(context, R.color.grey_11);
            textColor = ContextCompat.getColor(context, R.color.black);
            backgroundColor = ContextCompat.getColor(context, R.color.white);
        } else {
            editorTextColor = ContextCompat.getColor(context, R.color.grey_3);
            textColor = ContextCompat.getColor(context, R.color.grey_1);
            backgroundColor = ContextCompat.getColor(context, R.color.grey_11);
        }
    }

    ThemeDailyRecyclerViewAdapter(List<ThemeDailies> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == 1) {
            return TYPE_EDITOR;
        }
        if (dataList.get(position).getItem().getImages() == null) {
            return TYPE_ITEM_TV;
        } else {
            return TYPE_ITEM_TIV;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.theme_image, parent, false);
            return new ViewHolder(view, TYPE_HEADER);
        }
        if (viewType == TYPE_EDITOR) {
            View view = inflater.inflate(R.layout.theme_editors, parent, false);
            return new ViewHolder(view, TYPE_EDITOR);
        }
        if (viewType == TYPE_ITEM_TIV) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);
            return new ViewHolder(view, TYPE_ITEM_TIV);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.theme_item, parent, false);
            return new ViewHolder(view, TYPE_ITEM_TV);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        final ThemeDailies itemData = dataList.get(position);
        if (type == TYPE_HEADER) {
            Glide.with(context)
                    .load(itemData.getBackground())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.headerIV);
            holder.headerTV.setText(itemData.getDescription());
            return;
        }
        if (type == TYPE_EDITOR) {
            holder.editors.setEditors(itemData.getEditors());
            if (editorTextColor != 0) {
                holder.editors.setTextColor(editorTextColor);
            }
            return;
        }
        if (type == TYPE_ITEM_TV) {
            String text = itemData.getItem().getTitle();
            if (text.length() < 19) {
                text += "\n";
            }
            holder.noImageTV.setText(text);
            if (textColor != 0 && backgroundColor != 0) {
                holder.noImageTV.setTextColor(textColor);
                holder.noImageCardView.setCardBackgroundColor(backgroundColor);
            }
        }
        if (type == TYPE_ITEM_TIV) {
            holder.hasImageTV.setText(itemData.getItem().getTitle());
            Glide.with(context)
                    .load(itemData.getItem().getImages().get(0))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.itemIV);
            if (textColor != 0 && backgroundColor != 0) {
                holder.hasImageTV.setTextColor(textColor);
                holder.hasImageCardView.setCardBackgroundColor(backgroundColor);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("id", itemData.getItem().getId());
                intent.putExtra("isMainDaily", false);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()) {
            return 0;
        }
        return dataList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView hasImageCardView, noImageCardView;
        private TextView headerTV, hasImageTV, noImageTV;
        private ImageView headerIV, itemIV;
        private Editors editors;

        ViewHolder(View itemView, int type) {
            super(itemView);
            if (type == TYPE_HEADER) {
                headerIV = (ImageView) itemView.findViewById(R.id.image);
                headerTV = (TextView) itemView.findViewById(R.id.title);
                return;
            }
            if (type == TYPE_EDITOR) {
                editors = (Editors) itemView.findViewById(R.id.editors);
                return;
            }
            if (type == TYPE_ITEM_TIV) {
                hasImageCardView = (CardView) itemView.findViewById(R.id.cardView);
                hasImageTV = (TextView) itemView.findViewById(R.id.textView);
                itemIV = (ImageView) itemView.findViewById(R.id.imageView);
                return;
            }
            if (type == TYPE_ITEM_TV) {
                noImageCardView = (CardView) itemView.findViewById(R.id.cardView);
                noImageTV = (TextView) itemView.findViewById(R.id.textView);
            }
        }
    }
}
