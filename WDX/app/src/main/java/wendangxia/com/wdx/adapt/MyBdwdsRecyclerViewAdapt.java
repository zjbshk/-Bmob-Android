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

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.util.List;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.activity.BDWDActivity;
import wendangxia.com.wdx.activity.PreViewActivity;
import wendangxia.com.wdx.bean.BdwdA;
import wendangxia.com.wdx.util.SqliteUtil;
import wendangxia.com.wdx.util.ToastUtil;


public class MyBdwdsRecyclerViewAdapt extends RecyclerView.Adapter<MyBdwdsRecyclerViewAdapt.Handler> {


    private List<BdwdA> bdwdList;
    private Context context;
    private int resource;

    public MyBdwdsRecyclerViewAdapt(List<BdwdA> bdwdList, Context context, int resource) {
        this.bdwdList = bdwdList;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public Handler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new Handler(view, context, bdwdList, this);
    }

    @Override
    public void onBindViewHolder(Handler holder, int position) {
        if (bdwdList == null || bdwdList.isEmpty())
            holder.setAttribute(new BdwdA());
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
        private TextView item_time;
        private TextView product_sum;
        private TextView price;
        private ImageView sub;
        private ImageView add;
        private LinearLayout item_linear;
        private Context context;
        private List<BdwdA> bdwdList;
        private MyBdwdsRecyclerViewAdapt adapt;

        public Handler(View itemView, Context context, List<BdwdA> bdwdList, MyBdwdsRecyclerViewAdapt adapt) {
            super(itemView);
            this.context = context;
            this.adapt = adapt;
            this.bdwdList = bdwdList;
            document_icon = (ImageView) itemView.findViewById(R.id.document_icon);
            price = (TextView) itemView.findViewById(R.id.price);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
            item_time = (TextView) itemView.findViewById(R.id.item_time);
            product_sum = (TextView) itemView.findViewById(R.id.product_sum);
            sub = (ImageView) itemView.findViewById(R.id.sub);
            add = (ImageView) itemView.findViewById(R.id.add);
            product_sum = (TextView) itemView.findViewById(R.id.product_sum);
            item_linear = (LinearLayout) itemView.findViewById(R.id.item_linear);
        }


        public void setAttribute(final BdwdA bdwd) {
            String pic = bdwd.getPic();
            if (pic != null) {
                Glide.with(context).load(pic).into(document_icon);
            } else {
                document_icon.setImageResource(R.drawable.icon);
            }

            if (bdwd.getPrize() != 0) {
                price.setText("下载豆:" + bdwd.getPrize());
            } else {
                price.setVisibility(View.INVISIBLE);
            }
            if (bdwd.getNum() == 0) {
                add.setVisibility(View.GONE);
                sub.setVisibility(View.GONE);
                if (bdwd.getFw().equals(context.getResources().getString(R.string.mydownload))) {
                    product_sum.setText("待发货");
                } else {
                    product_sum.setVisibility(View.INVISIBLE);
                }
            } else {
                product_sum.setText(String.valueOf(bdwd.getNum()));
            }
            item_title.setText(bdwd.getTitle());
            item_time.setText(bdwd.getTime());
            item_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoPreViewActivity(bdwd);
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sum = product_sum.getText().toString();
                    bdwd.setNum(bdwd.getNum() + 1);
                    adapt.notifyDataSetChanged();
                }
            });
            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int sum = bdwd.getNum();
                    if (sum == 1) {
                        ToastUtil.showError(context, "最小一个哦,不能再减了");
                        return;
                    }
                    bdwd.setNum(bdwd.getNum() - 1);
                    adapt.notifyDataSetChanged();
                }
            });
            item_linear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                    builder.title(bdwd.getTitle());
                    builder.titleGravity(GravityEnum.CENTER);
                    builder.titleColorRes(R.color.colorAccent);
                    final String open = context.getResources().getString(R.string.open);
                    final String delete = context.getResources().getString(R.string.delete);
                    builder.items(open, delete);
                    builder.itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                            String textStr = text.toString();
                            if (textStr.equals(open)) {
                                gotoPreViewActivity(bdwd);
                            } else if (textStr.equals(delete)) {
                                deleteBdwd(bdwd);
                            }
                        }
                    });
                    builder.show();
                    return false;
                }
            });
            ((BDWDActivity) context).changeCount(bdwdList);
        }

        private void deleteBdwd(BdwdA bdwd) {
            if (bdwd.getFw().equals(context.getResources().getString(R.string.mydownload))
                    || bdwd.getFw().equals(context.getResources().getString(R.string.myrecond))) {
                SqliteUtil sqliteUtil = new SqliteUtil(context, "wd", null, 1);
                SqliteUtil.deleteBean(bdwd.getId(), bdwd.getFw(), sqliteUtil.getWritableDatabase());
                bdwdList.remove(bdwd);
                adapt.notifyDataSetChanged();
            } else {
                bdwdList.remove(bdwd);
                adapt.notifyDataSetChanged();
            }
        }

        private void gotoPreViewActivity(BdwdA bdwd) {
            Intent intent = new Intent(context, PreViewActivity.class);
            intent.putExtra("bdwd", bdwd);
            context.startActivity(intent);
            ((BDWDActivity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
}
