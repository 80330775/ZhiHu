package com.qinjunyuan.zhihu.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Editors extends LinearLayout {
    private Context context;
    private LayoutParams imageParams, textParams;
    private TextView textView;

    public Editors(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        int circleSize = (int) (context.getResources().getDisplayMetrics().density * 30 + 0.5f);
        int circleMarginStart = (int) (context.getResources().getDisplayMetrics().density * 15 + 0.5f);

        setBackgroundResource(R.drawable.touming1);
        textView = new TextView(context);
        textView.setText("小编");

        textParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER_VERTICAL;
        imageParams = new LayoutParams(circleSize, circleSize);
        imageParams.setMargins(circleMarginStart, 0, 0, 0);
    }

    public void setEditors(List<ThemeDailies.Editors> dataList) {
        if (!dataList.isEmpty()) {
            removeAllViews();
            addView(textView, textParams);
            for (int i = 0; i < dataList.size(); i++) {
                CircleImageView circleImageView = new CircleImageView(context);
                Glide.with(context).load(dataList.get(i).getAvatar()).into(circleImageView);
                addView(circleImageView, imageParams);
            }
        }
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }
}
