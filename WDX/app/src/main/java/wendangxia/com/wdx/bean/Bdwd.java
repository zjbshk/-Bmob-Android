package wendangxia.com.wdx.bean;

import java.net.MalformedURLException;
import java.util.Objects;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import wendangxia.com.wdx.util.Util;

/**
 * Created by Administrator on 2018/12/15.
 */

public class Bdwd extends BmobObject {

    private String id;
    private String url;
    private int prize;
    private String title;
    private String creater;
    private String pic;
    private String description;

    public Bdwd() {
        super();
    }

    public Bdwd(String id, String url, int prize, String title, String creater, String pic, String description) {
        this.id = id;
        this.url = url;
        this.prize = prize;
        this.title = title;
        this.creater = creater;
        this.pic = pic;
        this.description = description;
    }

    public String getId() {
        if (id == null && url == null) {
            return null;
        } else if (id == null) {
            try {
                return Util.getBaiduDocId(url);
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return id;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        if (url == null && id == null) {
            return null;
        } else if (url == null) {
            return "http://product.m.dangdang.com/product.php?pid=" + id;
        } else {
            return url;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Bdwd{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", prize=" + prize +
                ", creater='" + creater + '\'' +
                ", pic='" + pic + '\'' +
                ", description='" + description + '\'' +
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
