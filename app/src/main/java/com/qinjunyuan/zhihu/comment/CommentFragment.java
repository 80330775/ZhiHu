package com.qinjunyuan.zhihu.comment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.DataBean.DailyComment;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class CommentFragment extends Fragment implements CommentContract.View, CommentRecyclerViewAdapter.OnShortCommentsHeaderClickListener {
    private boolean isDay;
    private View view;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;
    private LinearLayoutManager linearManager;
    private Subscription subscription;

    private CommentContract.Presenter presenter;

    public static CommentFragment newInstance(boolean isDay) {
        Bundle args = new Bundle();
        args.putBoolean("isDay", isDay);
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            isDay = getArguments().getBoolean("isDay");
            view = inflater.inflate(R.layout.comment_fragment, container, false);
            toolbar = (Toolbar) view.findViewById(R.id.toolBar);
            toolbar.setTitle("");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            presenter.initRecyclerView();
        }
        if (container != null) {
            container.removeView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onDestroy() {
        Log.d("tjy", "commentFragment的onDestroy ");
        if (subscription != null) {
            subscription.unsubscribe();
            Log.d("tjy", "获取短评的subscription被unSubscribe() ");
        }
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void upDateToolBar(String commentCount) {
        Resources res = getResources();
        StringBuilder sb = new StringBuilder();
        sb.append(commentCount);
        sb.append(res.getString(R.string.comments));
        toolbar.setTitle(sb);
    }

    @Override
    public void initRecyclerView(List<DailyComment.Comment> dataList) {
        linearManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearManager);
        adapter = new CommentRecyclerViewAdapter(dataList, isDay);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new CommentItemDecoration(getContext()));
        adapter.setOnShortCommentsHeaderClickListener(this);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void shortCommentsViewClick(Observable<Void> observable) {
        subscription = observable.first()
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        presenter.getShortComments();
                    }
                });
    }

    @Override
    public void scrollToTop() {
        int position = linearManager.findLastVisibleItemPosition();
        int top = linearManager.findViewByPosition(position).getTop();
        recyclerView.scrollBy(0, top);
    }

    @Override
    public void setPresenter(CommentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void makeToast() {
        Toast.makeText(getContext(), "无法获取数据，请检查网络", Toast.LENGTH_SHORT).show();
    }
}
