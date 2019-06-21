package wendangxia.com.wdx.adapt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.activity.MainActivity;
import wendangxia.com.wdx.activity.PreViewActivity;
import wendangxia.com.wdx.activity.SearchActivity;
import wendangxia.com.wdx.bean.Bdwd;


public class MySearchRecyclerViewAdapt extends RecyclerView.Adapter<MySearchRecyclerViewAdapt.Handler> {


    private List<Bdwd> bdwdList;
    private Context context;
    private int resource;

    public MySearchRecyclerViewAdapt(List<Bdwd> bdwdList, Context context, int resource) {
        this.bdwdList = bdwdList;
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
        if (bdwdList == null || bdwdList.isEmpty())
            holder.setAttribute(new Bdwd());
        else holder.setAttribute(bdwdList.get(position));
    }

    @Override
    public int getItemCount() {
        if (bdwdList != null) {
            return bdwdList.size();
        }
        return 0;
    }

    static class Handler extends RecyclerView.ViewHolder {
        private ImageView document_icon;
        private TextView item_title;
        private TextView item_creater;
        private TextView item_price;
        private LinearLayout item_linear;
        private Context context;

        public Handler(View itemView, Context context) {
            super(itemView);
            this.context = context;
            document_icon = (ImageView) itemView.findViewById(R.id.document_icon);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
            item_creater = (TextView) itemView.findViewById(R.id.item_creater);
            item_price = (TextView) itemView.findViewById(R.id.item_price);
            item_linear = (LinearLayout) itemView.findViewById(R.id.item_linear);
        }

        public void setAttribute(final Bdwd bdwd) {

            item_title.setText(bdwd.getTitle());
            item_creater.setText(bdwd.getCreater());
            item_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PreViewActivity.class);
                    intent.putExtra("bdwd", bdwd);
                    context.startActivity(intent);
                    ((SearchActivity)context).overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
            });
        }
    }
}
