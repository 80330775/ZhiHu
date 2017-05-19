package com.qinjunyuan.zhihu.content;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.comment.CommentActivity;
import com.qinjunyuan.zhihu.data.DataBean.DailyContent;
import com.qinjunyuan.zhihu.data.DataBean.DailyExtra;
import com.qinjunyuan.zhihu.util.RxBus;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


public class ContentFragment extends Fragment implements ContentContract.View {
    private int id;
    private boolean type;
    private View view;
    private WebView webView;
    private ImageView imageView;
    private TextView title, source, comment, thumb;
    private static final String mimeType = "text/html;charset=UTF-8";

    private ContentContract.Presenter presenter;

    public static ContentFragment newInstance(int id, boolean isMainDaily) {
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putBoolean("type", isMainDaily);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_content_toolbar, menu);
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
            id = getArguments().getInt("id");
            type = getArguments().getBoolean("type");
            if (type) {
                view = inflater.inflate(R.layout.content_main, container, false);
                imageView = (ImageView) view.findViewById(R.id.imageView);
                title = (TextView) view.findViewById(R.id.title);
                source = (TextView) view.findViewById(R.id.source);
            } else {
                view = inflater.inflate(R.layout.content_theme, container, false);
            }
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolBar);
            toolbar.setTitle("");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
            comment = (TextView) view.findViewById(R.id.comment);
            thumb = (TextView) view.findViewById(R.id.thumb);
            webView = (WebView) view.findViewById(R.id.webView);
            RxBus.getInstance().addDisposable(this, RxBus.getInstance()
                    .toObservable(2, Boolean.class)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {
                            SharedPreferences.Editor editor = getActivity().getApplication().getSharedPreferences("size", 0).edit();
                            int contentWidth = imageView.getWidth();
                            int contentHeight = imageView.getHeight();
                            editor.putInt("content_width", contentWidth);
                            editor.putInt("content_height", contentHeight);
                            editor.apply();
                            Log.d("tjy", "content_width " + contentWidth);
                            Log.d("tjy", "content_height " + contentHeight);
                        }
                    }));
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
        Log.d("tjy", "contentFragment的onDestroy ");
        presenter.onDestroy();
        RxBus.getInstance().dispose(this);
        super.onDestroy();
    }

    @Override
    public void upDateToolBar(final DailyExtra extra) {
        if (extra != null) {
            comment.setText(extra.getComments());
            thumb.setText(String.valueOf(extra.getPopularity()));
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CommentActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("extra", extra);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void upDateWebView(DailyContent content) {
        if (type) {
            Glide.with(this)
                    .load(content.getImage())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            title.setText(content.getTitle());
            source.setText(content.getImage_source());
        }
        String body = content.getBody() + "<link rel=\"stylesheet\" href=\"file:///android_asset/content.css\" type=\"text/css\" />";
        webView.loadDataWithBaseURL("file:///android_assets/", body, mimeType, null, null);
    }

    @Override
    public void setPresenter(ContentContract.Presenter presenter) {
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
