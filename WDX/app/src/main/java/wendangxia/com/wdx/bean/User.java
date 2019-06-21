package wendangxia.com.wdx.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/12/14.
 */

public class User extends BmobObject {

    private String username;
    private String email;
    private String url;

    private String password;
    private int count;
    private int usecount;
    private String md5;
    private String level;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User() {
        super();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getUsecount() {
        return usecount;
    }

    public void setUsecount(int usecount) {
        this.usecount = usecount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void set(User userTmp) {
        if (userTmp != null) {
            setCount(userTmp.getCount());
            setLevel(userTmp.getLevel());
            setUsername(userTmp.getUsername());
            setPassword(userTmp.getPassword());
            setMd5(userTmp.getMd5());
            setEmail(userTmp.getEmail());
            setUrl(userTmp.getUrl());
            setUsecount(userTmp.getUsecount());
        }
    }
}
