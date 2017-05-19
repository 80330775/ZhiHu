package com.qinjunyuan.zhihu.main.main;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.MainDailies;
import com.qinjunyuan.zhihu.util.MyApplication;
import com.qinjunyuan.zhihu.util.RxBus;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class MainDailyFragment extends Fragment implements MainDailyContract.View, View.OnTouchListener {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MainDailyRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private View headerView;
    private ViewPager viewPager;
    private MainDailyViewPagerAdapter pagerAdapter;
    private CirclePageIndicator circlePageIndicator;
    private int upDateCount;
    private static final int msg = 0;
    private HandlerThread handlerThread;
    private Handler handler, UIHandler = new Handler();

    private View view;
    private CompositeSubscription subscription;

    @Inject
    MainDailyPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final boolean b = view == null;
        Log.d("tjy", "onCreateView: view == null 是 " + b + "的");
        if (view == null) {
            view = inflater.inflate(R.layout.main_fragment, container, false);
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            refreshLayout.setColorSchemeResources(R.color.toolbar);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    presenter.onRefresh();
                }
            });
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            headerView = inflater.inflate(R.layout.viewpager, null);
            viewPager = (ViewPager) headerView.findViewById(R.id.viewPager);
            viewPager.setOnTouchListener(this);
            circlePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.circlePageIndicator);

            handlerThread = new HandlerThread("tjy");
            handlerThread.start();
            handler = new MyHandler(this, handlerThread.getLooper());

            subscription = new CompositeSubscription();
            subscription.add(RxRecyclerView.scrollEvents(recyclerView)
                    .filter(new Func1<RecyclerViewScrollEvent, Boolean>() {
                        @Override
                        public Boolean call(RecyclerViewScrollEvent recyclerViewScrollEvent) {
                            return !recyclerView.canScrollVertically(1) && recyclerViewScrollEvent.dy() > 0;
                        }
                    })
                    .throttleFirst(2, TimeUnit.SECONDS)
                    .subscribe(new Action1<RecyclerViewScrollEvent>() {
                        @Override
                        public void call(RecyclerViewScrollEvent recyclerViewScrollEvent) {
                            Log.d("tjy", "recyclerView到达底部 " + !recyclerView.canScrollVertically(1));
                            Log.d("tjy", "recyclerView偏移量dy = " + recyclerViewScrollEvent.dy());
                            presenter.getBeforeDailies();
                        }
                    }));
            subscription.add(RxRecyclerView.scrollEvents(recyclerView)
                    .map(new Func1<RecyclerViewScrollEvent, Integer>() {
                        @Override
                        public Integer call(RecyclerViewScrollEvent recyclerViewScrollEvent) {
                            return linearLayoutManager.findFirstVisibleItemPosition();
                        }
                    })
                    .distinctUntilChanged()
                    .map(new Func1<Integer, String>() {
                        @Override
                        public String call(Integer integer) {
                            if (integer == 0) {
                                return "首页";
                            }
                            return recyclerViewAdapter.getDate(integer - 1);
                        }
                    })
                    .distinctUntilChanged()
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            RxBus.getInstance().post(0, s);
                        }
                    }));
        }
        if (container != null) {
            container.removeView(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DaggerMainDailyComponent
                .builder()
                .repositoryComponent(((MyApplication) getActivity().getApplication()).getRepositoryComponent())
                .mainDailyModule(new MainDailyModule(this))
                .build()
                .inject(this);
        presenter.initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
        handler.sendEmptyMessageDelayed(msg, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(msg);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        handlerThread.quit();
        if (subscription != null && subscription.hasSubscriptions()) {
            subscription.unsubscribe();
        }
        Log.d("tjy", "mainFragment的onDestroy ");
        super.onDestroy();
    }

    @Override
    public void initViewPager(List<MainDailies.TopStory> dataList) {
        if (pagerAdapter == null) {
            pagerAdapter = new MainDailyViewPagerAdapter(dataList);
            viewPager.setAdapter(pagerAdapter);
            circlePageIndicator.setViewPager(viewPager);
        } else {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initRecyclerView(List<MainDailies.Story> dataList) {
        final SharedPreferences sp = getActivity().getApplication().getSharedPreferences("size", 0);
        int VP_size = sp.getInt("large_width", 0);
        int RV_size = sp.getInt("small_width", 0);
        if (RV_size == 0 || VP_size == 0) {
            measureImageSize(sp);
        }
        recyclerViewAdapter = new MainDailyRecyclerViewAdapter(dataList);
        recyclerViewAdapter.setHeaderView(headerView);
        recyclerView.setAdapter(recyclerViewAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 25;
                outRect.right = 25;
                outRect.bottom = 25;

                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.top = 0;
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.bottom = 0;
                }
            }
        });
    }

    public void setThemeMode(boolean isDay) {
        recyclerViewAdapter.setThemeMode(isDay);
    }

    @Override
    public void notifyDataSetChanged() {
        recyclerViewAdapter.notifyDataSetChanged();
        upDateCount = linearLayoutManager.getItemCount();//从1开始数
    }

    @Override
    public void notifyItemRangeInserted(int upDateCount) {
        recyclerViewAdapter.notifyItemRangeInserted(this.upDateCount, upDateCount);
        this.upDateCount += upDateCount;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setPresenter(MainDailyContract.Presenter presenter) {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void makeToast() {
        Toast.makeText(getContext(), "无法获取数据，请检查网络", Toast.LENGTH_SHORT).show();
    }

    private void measureImageSize(final SharedPreferences sp) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("tjy", "onGlobalLayout:111111111111111111111111111111111111111111111111 ");
                ImageView view = (ImageView) linearLayoutManager.findViewByPosition(linearLayoutManager.findLastVisibleItemPosition()).findViewById(R.id.imageView);
                if (view != null) {
                    SharedPreferences.Editor editor = sp.edit();
                    int RV_width = view.getWidth();
                    int RV_height = view.getHeight();
                    if (RV_width != 0 && RV_height != 0) {
                        editor.putInt("small_width", RV_width);
                        editor.putInt("small_height", RV_height);
                        Log.d("tjy", "recyclerView图片的宽度 " + RV_width);
                        Log.d("tjy", "recyclerView图片的高度 " + RV_height);
                    }
                    int VP_width = viewPager.getWidth();
                    int VP_height = viewPager.getHeight();
                    if (VP_width != 0 && VP_height != 0) {
                        editor.putInt("large_width", VP_width);
                        editor.putInt("large_height", VP_height);
                        Log.d("tjy", "viewPager图片的宽度 " + VP_width);
                        Log.d("tjy", "viewPager图片的高度 " + VP_height);
                    }
                    editor.apply();

                    if (Build.VERSION.SDK_INT >= 16) {
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (handler.hasMessages(msg)) {
                    handler.removeMessages(msg);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(msg, 5000);
                break;
        }
        return false;
    }

    static class MyHandler extends Handler {
        private MainDailyFragment fragment;
        private int i;

        MyHandler(MainDailyFragment fragment, Looper looper) {
            super(looper);
            this.fragment = new WeakReference<>(fragment).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MainDailyFragment.msg) {
                fragment.UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        i = fragment.viewPager.getCurrentItem();
                        if (i < 4) {
                            i++;
                        } else {
                            i = 0;
                        }
                        fragment.viewPager.setCurrentItem(i);
                        fragment.handler.sendEmptyMessageDelayed(MainDailyFragment.msg, 5000);
                    }
                });
            }
        }
    }
}
