package com.qinjunyuan.zhihu.util;


public class RxMessage {
    private Object o;
    private int code;

    public RxMessage(int code, Object o) {
        this.code = code;
        this.o = o;
    }

    public int getCode() {
        return code;
    }

    public Object getO() {
        return o;
    }
}
