package com.qinjunyuan.zhihu.main.theme;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.ThemeDailies;
import com.qinjunyuan.zhihu.util.MyApplication;

import java.util.List;

import javax.inject.Inject;


public class ThemeDailyFragment extends Fragment implements ThemeDailyContract.View {
    private int id;
    private View view;
    private RecyclerView recyclerView;
    private ThemeDailyRecyclerViewAdapter adapter;

    @Inject
    ThemeDailyPresenter presenter;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("id", id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.main_fragment, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            SwipeRefreshLayout refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            refresh.setEnabled(false);
        }
        if (container != null) {
            container.removeView(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt("id");
        }
        DaggerThemeDailyComponent
                .builder()
                .repositoryComponent(((MyApplication) getActivity().getApplication()).getRepositoryComponent())
                .themeDailyModule(new ThemeDailyModule(id, this))
                .build()
                .inject(this);
        presenter.initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    @Override
    public void start() {
        presenter.start();
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "themeFragment的onDestroy ");
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(ThemeDailyContract.Presenter presenter) {

    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void initRecyclerView(List<ThemeDailies> list) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new ThemeDailyRecyclerViewAdapter(list, getContext());
        boolean isDay = getActivity().getSharedPreferences("themeMode", 0).getBoolean("isDay", true);
        adapter.setThemeMode(isDay);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 25;
                outRect.right = 25;
                outRect.bottom = 25;

                if (parent.getChildLayoutPosition(view) < 2) {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });
    }

    public void setThemeMode(boolean isDay) {
        if (adapter != null) {
            adapter.setThemeMode(isDay);
        }
    }

    @Override
    public void makeToast() {
        Toast.makeText(getContext(), "无法获取数据，请检查网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setId(int id) {
        this.id = id;
        if (presenter != null) {
            presenter.setId(id);
        }
    }
}
