package wendangxia.com.wdx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.adapt.MyRecyclerViewAdapt;
import wendangxia.com.wdx.adapt.MySlideRecyclerViewAdapt;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.Bdwd;
import wendangxia.com.wdx.bean.MyXy;
import wendangxia.com.wdx.bean.Selid_icon;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.ToastUtil;


public class MainActivity extends AppCompatActivity {

    private RecyclerView main_recyclerView;
    private MyRecyclerViewAdapt adapt;
    private SwipeRefreshLayout refresh;
    private LinearLayout toolbar;
    private CircleImageView profile_image;
    private DrawerLayout drawerlayout;
    private RecyclerView main_slide_recycler;
    private AVLoadingIndicatorView avloading;
    private ImageView userinfo_image_show;
    private ImageView recond_image_show;
    private LinearLayout search;

    private TextView username;
    private TextView download_dou;

    private List<Bdwd> alllist = new ArrayList<>();
    private int page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        avloading.setVisibility(View.VISIBLE);
        avloading.show();
        loadData(page, 20, true);
    }


    private void loadData(final int index, int pageSize, final boolean isAdd) {
        BmobQuery<Bdwd> query = new BmobQuery<>();
        query.setLimit(pageSize);
        query.setSkip(index * pageSize);
        query.order("-createdAt");
        query.findObjects(this, new FindListener<Bdwd>() {
            @Override
            public void onSuccess(List<Bdwd> list) {
                if (list.isEmpty()) {
                    ToastUtil.showSuccess(MainActivity.this, "数据已经全部加载");
                    return;
                }
                if (index == 0 && !isAdd) {
                    for (Bdwd bdwd : alllist) {
                        list.remove(bdwd);
                    }
                    if (list.isEmpty()) {
                        ToastUtil.showSuccess(MainActivity.this, "暂时无数据更新哦");
                    } else {
                        alllist.addAll(0, list);
                    }
                } else {
                    alllist.addAll(list);
                }
                adapt.notifyDataSetChanged();
                if (isAdd) {
                    page++;
                }
            }

            @Override
            public void onError(int i, String s) {
                if (s.contains(getResources().getString(R.string.nonetwork))) {
                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                } else {
                    ToastUtil.showError(MainActivity.this, "数据加载错误,原因:" + s);
                }
            }

            @Override
            public void onFinish() {
                avloading.setVisibility(View.GONE);
                avloading.hide();
                refresh.setRefreshing(false);
                super.onFinish();
            }
        });
    }

    private void initView() {
        drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        /**
         * 用户头像于下载豆等信息
         */
        username = (TextView) findViewById(R.id.username);
        download_dou = (TextView) findViewById(R.id.download_dou);

        /**
         * 侧边栏的recyclerView
         */
        main_slide_recycler = (RecyclerView) findViewById(R.id.main_slide_recycler);
        main_slide_recycler.setLayoutManager(new LinearLayoutManager(this));
        List<Selid_icon> selid_icons = Arrays.asList(
                new Selid_icon(1, getResources().getString(R.string.personcenter), R.drawable.person_icon),
                new Selid_icon(2, getResources().getString(R.string.mycollect), R.drawable.collect_icon),
                new Selid_icon(3, getResources().getString(R.string.mydownload), R.drawable.download),
                new Selid_icon(4, getResources().getString(R.string.aboutus), R.drawable.about_icon));
        MySlideRecyclerViewAdapt sadapt = new MySlideRecyclerViewAdapt(selid_icons, this, R.layout.recycle_slide_item);
        main_slide_recycler.setAdapter(sadapt);

        userinfo_image_show = (ImageView) toolbar.findViewById(R.id.userinfo_image_show);
        recond_image_show = (ImageView) toolbar.findViewById(R.id.recond_image_show);
        search = (LinearLayout) toolbar.findViewById(R.id.search);
        userinfo_image_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerlayout.isDrawerOpen(GravityCompat.START))
                    drawerlayout.openDrawer(GravityCompat.START, true);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                MainActivity.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        recond_image_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BDWDActivity.class);
                MyXy xy = new MyXy();
                xy.setStatus(200);
                xy.setRemark("0");
                xy.setMsg(getResources().getString(R.string.myrecond));
                intent.putExtra("MyXy", xy);
                startActivity(intent);
                MainActivity.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        avloading = (AVLoadingIndicatorView) findViewById(R.id.avloading);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeColors(Color.parseColor("#FF4081"), Color.parseColor("#303F9F"), Color.parseColor("#FF4081"), Color.parseColor("#3F51B5"));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0, 20, false);
            }
        });

        /**
         * 主recyclerView初始化
         */
        main_recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        main_recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapt = new MyRecyclerViewAdapt(alllist, MainActivity.this, R.layout.recycle_item);
        main_recyclerView.setAdapter(adapt);
        main_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    avloading.setVisibility(View.VISIBLE);
                    avloading.show();
                    loadData(page, 20, true);
                }
            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication application = (MyApplication) getApplication();
                if (application.getUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, PersonActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication application = (MyApplication) getApplication();
        final User user = application.getUser();
        if (user != null) {
            if (user.getUrl() != null && user.getUrl().length() != 0) {
                Glide.with(this)
                        .load(user.getUrl())
                        .into(profile_image);
            }
            username.setText(user.getUsername());
            download_dou.setText("下载豆:" + user.getCount() + "个");
        } else {
            username.setText("未登录");
            download_dou.setText("下载豆");
            profile_image.setImageResource(R.drawable.icon_circle);
        }
    }
}
