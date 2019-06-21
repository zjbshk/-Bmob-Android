package wendangxia.com.wdx.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wendangxia.com.wdx.activity.BDWDActivity;
import wendangxia.com.wdx.bean.BdwdA;
import wendangxia.com.wdx.bean.Collect;
import wendangxia.com.wdx.bean.MyXy;
import wendangxia.com.wdx.bean.User;

/**
 * Created by Administrator on 2018/12/17.
 */

public class BdwdsUtil {
    public static List<BdwdA> getBDWDS(User user, MyXy myXy, BDWDActivity bdwdActivity, int page, int pageSize) {
        String from = myXy.getMsg();
        SqliteUtil sqliteUtil = new SqliteUtil(bdwdActivity, "wd", null, 1);
        SQLiteDatabase database = sqliteUtil.getWritableDatabase();
        String sql = "select id,title,time,fw,num,pic,url from bdwd where fw='" + from + "'";
        Cursor cursor = database.rawQuery(sql, null);
        List<BdwdA> bdwdAs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String time = cursor.getString(2);
                String fw = cursor.getString(3);
                int num = cursor.getInt(4);
                String pic = cursor.getString(5);
                String url = cursor.getString(6);
                BdwdA bdwdA = new BdwdA();

                bdwdA.setId(id);
                bdwdA.setTitle(title);
                bdwdA.setTime(time);
                bdwdA.setFw(fw);
                bdwdA.setPic(pic);
                bdwdA.setUrl(url);
                bdwdAs.add(bdwdA);
            } while (cursor.moveToNext());
        }
        System.out.println("bdwdAs.size:" + bdwdAs.size());
        return bdwdAs;
    }

    public static void getBDWDS(User user, MyXy myXy, BDWDActivity bdwdActivity, int page, int pageSize, FindListener<Collect> findListener) {
        BmobQuery<Collect> query = new BmobQuery<>();
        query.setLimit(pageSize);
        query.setSkip(pageSize * page);
        query.order("-createdAt");
        query.addWhereEqualTo("email", user.getEmail());
        query.findObjects(bdwdActivity, findListener);
    }
}
