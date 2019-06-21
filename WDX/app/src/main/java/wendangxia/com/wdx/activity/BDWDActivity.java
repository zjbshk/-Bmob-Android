package wendangxia.com.wdx.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a.a.V;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.FindListener;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.adapt.MyBdwdsRecyclerViewAdapt;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.BdwdA;
import wendangxia.com.wdx.bean.Collect;
import wendangxia.com.wdx.bean.MyXy;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.BdwdsUtil;
import wendangxia.com.wdx.util.SqliteUtil;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class BDWDActivity extends AppCompatActivity {


    private RecyclerView bdwd_recycler;
    private MyXy myXy;
    private ImageView bdwd_empty;
    private TextView products_sum;
    private TextView prices_sum;
    private LinearLayout buy;
    private List<BdwdA> bdwdList = new ArrayList<>();
    private int page;
    private LinearLayout show_gowuche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdwd);
        myXy = (MyXy) getIntent().getSerializableExtra("MyXy");
        if (myXy == null || myXy.getRemark() == null) {
            ToastUtil.showError(this, "无权进入该页面");
            return;
        }
        setTitle(myXy.getMsg());
        initView();
        loadData(20);
    }

    private void loadData(int pageSize) {
        MyApplication application = (MyApplication) getApplication();
        User user = application.getUser();
        if (myXy.getRemark().equals("2")) {
            FindListener<Collect> findListener = new FindListener<Collect>() {
                @Override
                public void onSuccess(List<Collect> list) {
                    if (!list.isEmpty()) {
                        for (Collect collect : list) {
                            BdwdA bdwda = new BdwdA();
                            bdwda.setTitle(collect.getTitle());
                            bdwda.setPic(collect.getPic());
                            bdwda.setUrl(collect.getUrl());
                            bdwda.setId(collect.getId());
                            bdwda.setNum(collect.getNum());
                            bdwda.setTime(collect.getCreatedAt());
                            bdwda.setPrize(collect.getPrice());
                            bdwda.setFw(getResources().getString(R.string.mycollect));
                            bdwdList.add(bdwda);
                        }
                        changeCount(bdwdList);
                        bdwd_recycler.getAdapter().notifyDataSetChanged();
                        bdwd_empty.setVisibility(View.GONE);
                        bdwd_recycler.setVisibility(View.VISIBLE);
                        show_gowuche.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtil.showSuccess(BDWDActivity.this, "数据已经全部加载完毕");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    if (s.contains(getResources().getString(R.string.nonetwork))) {
                        ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                    } else {
                        ToastUtil.showError(BDWDActivity.this, "数据加载错误,原因:" + s);
                    }
                }
            };
            BdwdsUtil.getBDWDS(user, myXy, this, page, pageSize, findListener);
        } else {
            List<BdwdA> bdwdsTmp = BdwdsUtil.getBDWDS(user, myXy, this, page, pageSize);
            if (bdwdsTmp != null && !bdwdsTmp.isEmpty()) {
                bdwdList.addAll(bdwdsTmp);
                bdwd_recycler.getAdapter().notifyDataSetChanged();
                bdwd_empty.setVisibility(View.GONE);
                bdwd_recycler.setVisibility(View.VISIBLE);
            } else {
                ToastUtil.showSuccess(this, "数据已经全部加载完毕");
            }
        }
    }

    public void changeCount(List<BdwdA> list) {
        int sum = 0;
        int sum_product = 0;
        for (BdwdA collect : list) {
            sum += collect.getNum() * collect.getPrize();
            sum_product += collect.getNum();
        }
        products_sum.setText("宝贝数:" + sum_product);
        prices_sum.setText("合计:" + sum);
    }

    private void initView() {
        final ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.back);
        bdwd_recycler = (RecyclerView) findViewById(R.id.bdwd_recycler);
        bdwd_empty = (ImageView) findViewById(R.id.bdwd_empty);
        products_sum = (TextView) findViewById(R.id.products_sum);
        prices_sum = (TextView) findViewById(R.id.prices_sum);
        buy = (LinearLayout) findViewById(R.id.buy);
        show_gowuche = (LinearLayout) findViewById(R.id.show_gowuche);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (BdwdA bdwdA : bdwdList) {
                    downloadRecord(bdwdA);
                }
                ToastUtil.showError(BDWDActivity.this, "购物车已经清空");
                bdwdList.clear();
                bdwd_recycler.getAdapter().notifyDataSetChanged();
            }
        });
        bdwd_recycler.setLayoutManager(new LinearLayoutManager(this));
        MyBdwdsRecyclerViewAdapt adapt = new MyBdwdsRecyclerViewAdapt(bdwdList, this, R.layout.recycle_bdwks_item);
        bdwd_recycler.setAdapter(adapt);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    private void downloadRecord(BdwdA bdwd) {
        String from = getResources().getString(R.string.mydownload);
        myRecord(from, bdwd);
    }

    private void myRecord(String from, BdwdA bdwd) {
        SqliteUtil sqliteUtil = new SqliteUtil(this, "wd", null, 1);
        if (SqliteUtil.selectBeanCount(bdwd.getId(), from, sqliteUtil.getWritableDatabase()) == 0) {
            BdwdA bdwdA = new BdwdA();
            bdwdA.setId(bdwd.getId());
            bdwdA.setNum(1);
            bdwdA.setPic(bdwd.getPic());
            bdwdA.setPrize(bdwd.getPrize());
            bdwdA.setTitle(bdwd.getTitle());
            bdwdA.setUrl(bdwd.getUrl());
            bdwdA.setFw(from);
            bdwdA.setTime(Util.getFormatTime());
            SqliteUtil.insertBean(bdwdA, sqliteUtil.getWritableDatabase());
        } else {
            SqliteUtil.updateBeanTime(bdwd.getId(), from, Util.getFormatTime(), sqliteUtil.getWritableDatabase());
        }
    }
}
