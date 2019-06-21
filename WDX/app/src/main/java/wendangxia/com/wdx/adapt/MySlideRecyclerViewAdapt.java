package wendangxia.com.wdx.adapt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.activity.BDWDActivity;
import wendangxia.com.wdx.activity.LoginActivity;
import wendangxia.com.wdx.activity.MainActivity;
import wendangxia.com.wdx.activity.PersonActivity;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.MyXy;
import wendangxia.com.wdx.bean.Selid_icon;
import wendangxia.com.wdx.util.ToastUtil;


public class MySlideRecyclerViewAdapt extends RecyclerView.Adapter<MySlideRecyclerViewAdapt.Handler> {


    private List<Selid_icon> selid_icons;
    private Context context;
    private int resource;

    public MySlideRecyclerViewAdapt(List<Selid_icon> selid_icons, Context context, int resource) {
        this.selid_icons = selid_icons;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public Handler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new Handler(view, context);
    }

    @Override
    public void onBindViewHolder(Handler holder, int position) {
        if (selid_icons == null || selid_icons.isEmpty())
            holder.setAttribute(new Selid_icon());
        else holder.setAttribute(selid_icons.get(position));
    }

    @Override
    public int getItemCount() {
        if (selid_icons != null) {
            return selid_icons.size();
        }
        return 0;
    }

    static class Handler extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView tip;
        private LinearLayout item_linear;
        private Context context;

        public Handler(View itemView, Context context) {
            super(itemView);
            this.context = context;
            icon = (ImageView) itemView.findViewById(R.id.icon);
            tip = (TextView) itemView.findViewById(R.id.tip);
            item_linear = (LinearLayout) itemView.findViewById(R.id.item_linear);
        }

        public void setAttribute(final Selid_icon selid_icon) {
            tip.setText(selid_icon.getName());
            icon.setImageResource(selid_icon.getIconResId());
            final MainActivity activity = ((MainActivity) (context));
            final MyApplication application = (MyApplication) activity.getApplication();

            item_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selid_icon.getId() == 2 || selid_icon.getId() == 3 || selid_icon.getId() == 0) {
                        if (application.getUser() == null && selid_icon.getId() == 2) {
                            ToastUtil.showError(activity, context.getResources().getString(R.string.login_tip));
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, BDWDActivity.class);
                            MyXy xy = new MyXy();
                            xy.setStatus(200);
                            xy.setRemark(selid_icon.getId() + "");
                            xy.setMsg(selid_icon.getName());
                            intent.putExtra("MyXy", xy);
                            context.startActivity(intent);
                            ((MainActivity) (context)).overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        }
                    } else if (selid_icon.getId() == 1) {
                        if (application.getUser() == null) {
                            ToastUtil.showError(activity, context.getResources().getString(R.string.login_tip));
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, PersonActivity.class);
                            context.startActivity(intent);
                            ((MainActivity) (context)).overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        }
                    } else if (selid_icon.getId() == 4) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                        builder.title(selid_icon.getName());
                        builder.titleGravity(GravityEnum.CENTER);
                        builder.content(context.getResources().getString(R.string.us));
                        builder.titleColor(Color.parseColor("#FF4081"));
                        builder.show();
                    }
                }
            });
        }
    }
}
