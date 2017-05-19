package com.qinjunyuan.zhihu.main.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
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


class MainDailyViewPagerAdapter extends PagerAdapter {
    private List<MainDailies.TopStory> dataList;
    private Context context;

    MainDailyViewPagerAdapter(List<MainDailies.TopStory> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (context == null) {
            context = container.getContext();
        }
        final MainDailies.TopStory pageData = dataList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        Glide.with(context)
                .load(pageData.getImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        textView.setText(pageData.getTitle());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentActivity.class);
                intent.putExtra("id", pageData.getId());
                intent.putExtra("type", true);
                context.startActivity(intent);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
