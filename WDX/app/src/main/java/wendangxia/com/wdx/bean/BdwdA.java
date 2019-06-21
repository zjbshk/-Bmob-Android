package wendangxia.com.wdx.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/12/15.
 */

public class BdwdA extends Bdwd {

    private String time;
    private String fw;
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    @Override
    public String toString() {
        return "Bdwd{" +
                "id='" + getId() + '\'' +
                ", url='" + getUrl() + '\'' +
                ", prize=" + getPrize() +
                ", creater='" + getCreater() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", time='" + getTime() + '\'' +
                ", fw='" + getFw() + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object bdwd) {
        if (bdwd == null) {
            return false;
        }
        return bdwd.toString().equals(toString());
    }
}
