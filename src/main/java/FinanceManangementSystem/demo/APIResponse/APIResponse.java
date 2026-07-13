package FinanceManangementSystem.demo.APIResponse;

public class APIResponse<T> {
    private String msg;
    private T data;

    public APIResponse(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
