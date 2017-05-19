package com.qinjunyuan.zhihu.util;


import android.util.Log;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {
    private Subject<Object> behaviorSubject;
    private HashMap<String, CompositeDisposable> map;

    private RxBus() {
        behaviorSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o) {
        behaviorSubject.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> type) {
        return behaviorSubject.ofType(type);
    }

    public void post(int code, Object o) {
        behaviorSubject.onNext(new RxMessage(code, o));
    }

    public <T> Observable<T> toObservable(final int code, final Class<T> type) {
        return behaviorSubject.ofType(RxMessage.class)
                .filter(new Predicate<RxMessage>() {
                    @Override
                    public boolean test(@NonNull RxMessage rxMessage) throws Exception {
                        return code == rxMessage.getCode() && type.isInstance(rxMessage.getO());
                    }
                })
                .map(new Function<RxMessage, Object>() {
                    @Override
                    public Object apply(@NonNull RxMessage rxMessage) throws Exception {
                        return rxMessage.getO();
                    }
                })
                .cast(type);
    }

    public void addDisposable(Object o, Disposable disposable) {
        if (map == null) {
            map = new HashMap<>();
        }
        String key = o.getClass().getName();
        if (map.get(key) != null) {
            map.get(key).add(disposable);
        } else {
            CompositeDisposable cd = new CompositeDisposable();
            cd.add(disposable);
            map.put(key, cd);
        }
    }

    public void dispose(Object o) {
        if (map == null) {
            return;
        }
        String key = o.getClass().getName();
        if (!map.containsKey(key)) {
            return;
        }
        if (map.get(key) != null) {
            map.get(key).clear();
            Log.d("tjy", "RxBus.dispose() " + o);
        }
        map.remove(key);
    }

    public static RxBus getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final RxBus INSTANCE = new RxBus();
    }
}
