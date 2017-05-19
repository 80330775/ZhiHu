package com.qinjunyuan.zhihu.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.SkidMenu;
import com.qinjunyuan.zhihu.util.RxBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

class MainActivityRecyclerViewAdapter extends RecyclerView.Adapter<MainActivityRecyclerViewAdapter.MyViewHolder> {
    private static final int TYPE_HEADER = 0, TYPE_MAIN = 1, TYPE_THEME = 2;
    private List<SkidMenu.Info> dataList;
    private Context context;
    private int currentPosition = 1;
    private int headerBackground, headerTextColor, checkColor, unCheckColor, themeTextColor;

    void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    int getCurrentPosition() {
        return currentPosition;
    }

    void setThemeMode(boolean isDay) {
        if (isDay) {
            headerBackground = ContextCompat.getColor(context, R.color.toolbar);
            headerTextColor = ContextCompat.getColor(context, R.color.white);
            checkColor = ContextCompat.getColor(context, R.color.grey_1);
            unCheckColor = ContextCompat.getColor(context, R.color.white);
            themeTextColor = ContextCompat.getColor(context, R.color.black);
        } else {
            headerBackground = ContextCompat.getColor(context, R.color.grey_13);
            headerTextColor = ContextCompat.getColor(context, R.color.grey_1);
            checkColor = ContextCompat.getColor(context, R.color.grey_12);
            unCheckColor = ContextCompat.getColor(context, R.color.grey_11);
            themeTextColor = ContextCompat.getColor(context, R.color.grey_1);
        }
    }

    private OnItemClickListener listener;

    interface OnItemClickListener {
        void onItemClick(int id, String name);

        void download();
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    MainActivityRecyclerViewAdapter(List<SkidMenu.Info> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == 1) {
            return TYPE_MAIN;
        }
        return TYPE_THEME;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.skid_menu_header, parent, false);
            return new MyViewHolder(view, TYPE_HEADER);
        }
        if (viewType == TYPE_MAIN) {
            View view = LayoutInflater.from(context).inflate(R.layout.skid_menu_main, parent, false);
            return new MyViewHolder(view, TYPE_MAIN);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.skid_menu_theme, parent, false);
        return new MyViewHolder(view, TYPE_THEME);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.itemView.setBackgroundColor(headerBackground);
            holder.login.setTextColor(headerTextColor);
            holder.collect.setTextColor(headerTextColor);
            holder.download.setTextColor(headerTextColor);
            holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.download();
                }
            });
            RxBus.getInstance().addDisposable(this, RxBus.getInstance().toObservable(1, String.class)
                    .sample(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s) throws Exception {
                            if (s.equals("100%")) {
                                holder.download.setText("完成");
                                return;
                            }
                            holder.download.setText(s);
                        }
                    }));
        }
        if (getItemViewType(position) == TYPE_MAIN) {
            if (position == currentPosition) {
                holder.itemView.setBackgroundColor(checkColor);
            } else {
                holder.itemView.setBackgroundColor(unCheckColor);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(checkColor);
                    listener.onItemClick(1, "首页");
                    currentPosition = 1;
                }
            });
        }
        if (getItemViewType(position) == TYPE_THEME) {
            if (position == currentPosition) {
                holder.itemView.setBackgroundColor(checkColor);
            } else {
                holder.itemView.setBackgroundColor(unCheckColor);
            }
            holder.textView.setTextColor(themeTextColor);
            holder.textView.setText(dataList.get(position - 2).getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition = holder.getAdapterPosition();
                    v.setBackgroundColor(checkColor);
                    listener.onItemClick(dataList.get(mPosition - 2).getId(), dataList.get(mPosition - 2).getName());
                    currentPosition = mPosition;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (dataList.isEmpty()) {
            return 0;
        }
        return dataList.size() + 2;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, login, collect, download;

        MyViewHolder(View itemView, int type) {
            super(itemView);
            switch (type) {
                case TYPE_HEADER:
                    login = (TextView) itemView.findViewById(R.id.login);
                    collect = (TextView) itemView.findViewById(R.id.collect);
                    download = (TextView) itemView.findViewById(R.id.download);
                    break;
                case TYPE_MAIN:
                    break;
                case TYPE_THEME:
                    textView = (TextView) itemView.findViewById(R.id.textView);
                    break;
            }
        }

    }
}