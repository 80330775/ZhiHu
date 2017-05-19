package com.qinjunyuan.zhihu.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinjunyuan.zhihu.R;
import com.qinjunyuan.zhihu.data.Repository;
import com.qinjunyuan.zhihu.main.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class WelcomeActivity extends AppCompatActivity {
    private CompositeDisposable disposable;
    private ImageView imageView;
    private WelcomeView view;
    private ZhiHuLogoAnimator logoAnimator;
//    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        disposable = new CompositeDisposable();
        imageView = (ImageView) findViewById(R.id.imageView);
        view = (WelcomeView) findViewById(R.id.customView);
        logoAnimator = (ZhiHuLogoAnimator) findViewById(R.id.customViewAnimator);
//        sp = getSharedPreferences("ZhiHu", 0);
//        long time = sp.getLong("time", 0);
//        if (time != 0) {
//            long currentTime = new Date().getTime();
//            if (currentTime < time) {
//                startMainActivity();
//            } else {
                showWelcomeImage();
//            }
//        } else {
//            showWelcomeImage();
//        }
    }

    private void showWelcomeImage() {
        Repository repository = ((MyApplication) getApplication()).getRepositoryComponent().provideRepository();
        disposable.add(repository.getWelcomeImageURL()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Glide.with(WelcomeActivity.this).load(s).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
//                        SharedPreferences.Editor editor = sp.edit();
//                        Log.d("tjy", "accept: " + new Date().getTime());
//                        long time = new Date().getTime() + 3 * 60 * 60 * 1000;
//                        long time = new Date().getTime() + 60 * 1000;
//                        editor.putLong("time", time);
//                        editor.apply();
                        float currentY = view.getTranslationY();
                        ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "TranslationY", currentY, 0f).setDuration(500);
                        ObjectAnimator rotate = ObjectAnimator.ofInt(logoAnimator, "Angle", 0, 270).setDuration(1500);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.play(rotate).after(moveIn);
                        animatorSet.start();

                        disposable.add(Completable.create(new CompletableOnSubscribe() {
                            @Override
                            public void subscribe(CompletableEmitter e) throws Exception {
                                e.onComplete();
                            }
                        }).delay(3, TimeUnit.SECONDS)
                                .subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        startMainActivity();
                                    }
                                }));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        startMainActivity();
                    }
                }));
    }

    private void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
