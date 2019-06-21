package wendangxia.com.wdx.application;

import android.app.Application;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

/**
 * Created by Administrator on 2018/12/17.
 */

public class MyApplication extends Application {


    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, "8cd2cc2dbb8af42210d767117da03316");
        String md5 = Util.getKeyValue(getApplicationContext(), "userinfo", "md5", "nouser");
        if (md5.equals("nouser")) {
            return;
        }
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("md5", md5);
        query.setLimit(1);
        query.findObjects(getApplicationContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (!list.isEmpty()) {
                    user = list.get(0);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (s.contains(getResources().getString(R.string.nonetwork))) {
                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                } else {
                    ToastUtil.showError(getApplicationContext(), "信息查询出错:" + s);
                }
            }
        });

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
