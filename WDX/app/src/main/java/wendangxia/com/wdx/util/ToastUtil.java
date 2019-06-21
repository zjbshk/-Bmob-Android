package wendangxia.com.wdx.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.activity.LoginActivity;

/**
 * Created by Administrator on 2018/12/14.
 */
public class ToastUtil {
    public static void showError(Context context, String s) {
        show(context,R.layout.toast_error,Toast.LENGTH_SHORT,s);
    }

    public static void showSuccess(Context context, String s) {
        show(context,R.layout.toast_error,Toast.LENGTH_SHORT,s);
    }
    public static void show(Context context,int resource,int duration, String s) {
        View view = LayoutInflater.from(context).inflate(resource,null);
        TextView tip = (TextView) view.findViewById(R.id.tip);
        tip.setText(s);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER,0,100);
        toast.setDuration(duration);
        toast.show();
    }
}
