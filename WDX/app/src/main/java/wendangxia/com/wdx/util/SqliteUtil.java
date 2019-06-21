package wendangxia.com.wdx.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.bean.BdwdA;

/**
 * Created by Administrator on 2018/12/18.
 */

public class SqliteUtil extends SQLiteOpenHelper {

    private Context context;

    public SqliteUtil(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(context.getResources().getString(R.string.table_create));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public static boolean insertBean(BdwdA bdwdA, SQLiteDatabase database) {

        String sql = "insert into bdwd(id,title,type,fw,time,url,num,pic) values(?,?,?,?,?,?,?,?)";
        Object[] values = new Object[8];
        values[0] = bdwdA.getId();
        values[1] = bdwdA.getTitle();
        values[3] = bdwdA.getFw();
        values[4] = bdwdA.getTime();
        values[5] = bdwdA.getUrl();
        values[6] = bdwdA.getNum();
        values[7] = bdwdA.getPic();
        database.execSQL(sql, values);
        return true;
    }

    public static int selectBeanCount(String id, String from, SQLiteDatabase database) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from bdwd where id='" + id + "' and fw='" + from + "'");
        String sql = sb.toString();
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            System.out.println(count);
            return count;
        } else {
            return 0;
        }
    }

    public static boolean deleteBean(String id, String from, SQLiteDatabase database) {
        if (id == null || from == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("delete from bdwd where id='" + id + "' and fw='" + from + "'");
        String sql = sb.toString();
        System.out.println(sql);
        database.execSQL(sql);
        return true;
    }

    public static boolean updateBeanTime(String id, String from, String time, SQLiteDatabase database) {
        if (id == null || from == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("update bdwd set time='" + time + "' where id='" + id + "' and fw='" + from + "'");
        String sql = sb.toString();
        database.execSQL(sql);
        return true;
    }
}
