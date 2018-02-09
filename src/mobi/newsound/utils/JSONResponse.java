package mobi.newsound.utils;

import com.google.gson.annotations.Expose;

public class JSONResponse<T> {

    @Expose
    private int code;

    @Expose
    private String message;

    @Expose
    private T data;

    public JSONResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}
