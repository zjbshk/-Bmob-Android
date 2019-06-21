package wendangxia.com.wdx.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/12/15.
 */

public class Preview extends BmobObject {

    private String key;
    private String value;

    public Preview() {
        super();
    }

    public Preview(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
