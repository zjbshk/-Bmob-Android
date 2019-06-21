package wendangxia.com.wdx.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/12/15.
 */

public class Collect extends BmobObject {

    private String email;
    private String title;
    private String url;
    private String id;
    private String pic;
    private int price;
    private int num;


    public Collect() {
        super();
    }

    public Collect(String email, String title, String url, String id, String pic, int price, int num) {
        this.email = email;
        this.title = title;
        this.url = url;
        this.id = id;
        this.pic = pic;
        this.price = price;
        this.num = num;
    }

    public Collect(String email, String title, String url, String pic, int price, int num) {
        this.email = email;
        this.title = title;
        this.url = url;
        this.pic = pic;
        this.price = price;
        this.num = num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNum() {
        return num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Collect{" +
                "email='" + email + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", pic='" + pic + '\'' +
                ", price=" + price +
                ", num=" + num +
                '}';
    }
}
