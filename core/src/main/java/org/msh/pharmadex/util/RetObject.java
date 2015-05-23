package org.msh.pharmadex.util;

/**
 * Created by utkarsh on 1/18/15.
 */
public class RetObject {

    private String msg;

    private Object obj;

    public RetObject() {
    }

    public RetObject(String msg) {
        this.msg = msg;
    }

    public RetObject(String msg, Object obj) {
        this.msg = msg;
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
