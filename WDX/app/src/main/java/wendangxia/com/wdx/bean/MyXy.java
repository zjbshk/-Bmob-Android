package wendangxia.com.wdx.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/12/14.
 */

public class MyXy implements Serializable {

    private int status;
    private String msg;
    private String remark;

    public MyXy() {
        super();
    }

    public MyXy(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public MyXy(int status, String msg, String remark) {
        this.status = status;
        this.msg = msg;
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MyXy{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
