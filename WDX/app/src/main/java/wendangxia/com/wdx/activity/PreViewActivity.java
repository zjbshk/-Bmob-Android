package wendangxia.com.wdx.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.Bdwd;
import wendangxia.com.wdx.bean.BdwdA;
import wendangxia.com.wdx.bean.Collect;
import wendangxia.com.wdx.bean.MyXy;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.BdwdsUtil;
import wendangxia.com.wdx.util.SqliteUtil;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class PreViewActivity extends AppCompatActivity {

    private WebView preview_wd_web;
    private LinearLayout collect;
    private LinearLayout send;
    private LinearLayout download;
    private ImageView collect_image;
    private ImageView send_image;
    private AVLoadingIndicatorView avloading;
    private Bdwd bdwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        Intent intent = getIntent();
        bdwd = (Bdwd) intent.getSerializableExtra("bdwd");
        if (bdwd == null || bdwd.getUrl() == null) {
            return;
        } else {
            initView();
            loadData();
            browseRecord();
        }
        if (bdwd.getTitle() != null) {
            setTitle(bdwd.getTitle());
        }
    }

    private void browseRecord() {
        String from = getResources().getString(R.string.myrecond);
        myRecord(from);
    }

    private void downloadRecord() {
        String from = getResources().getString(R.string.mydownload);
        myRecord(from);
    }

    private void myRecord(String from) {
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


    private void loadData() {
        MyApplication application = (MyApplication) getApplication();
        User user = application.getUser();
        if (user != null) {
            try {
                System.out.println(bdwd.getUrl());
                String id = Util.getDDProductId(bdwd.getUrl(), "pid");
                BmobQuery<Collect> query = new BmobQuery<>();
                query.addWhereEqualTo("email", user.getEmail());
                query.addWhereEqualTo("id", id);
                query.count(this, Collect.class, new CountListener() {
                    @Override
                    public void onSuccess(int i) {
                        if (i >= 1) {
                            collect_image.setImageResource(R.drawable.che);
                            collect_image.setTag("collect");
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        System.out.println("检测是否收藏失败,原因:" + s);
                    }
                });
            } catch (MalformedURLException e) {
                return;
            }
        }
    }

    private void initView() {
        final ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeAsUpIndicator(R.drawable.back);
        avloading = (AVLoadingIndicatorView) findViewById(R.id.avloading);
        preview_wd_web = (WebView) findViewById(R.id.preview_wd_web);
        download = (LinearLayout) findViewById(R.id.download);
        collect = (LinearLayout) findViewById(R.id.collect);
        send = (LinearLayout) findViewById(R.id.send);
        send_image = (ImageView) findViewById(R.id.send_image);
        collect_image = (ImageView) findViewById(R.id.collect_image);
        final String jsStr = Util.<String>getKeyValue(this, "initdata", "jscode", "console.log(document.body.innerHTML)");
        preview_wd_web.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                avloading.setVisibility(View.VISIBLE);
                avloading.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                avloading.setVisibility(View.INVISIBLE);
                avloading.hide();
                preview_wd_web.evaluateJavascript(jsStr, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        System.out.println(value);
                    }
                });
            }
        });

        preview_wd_web.getSettings().setJavaScriptEnabled(true);
        preview_wd_web.loadUrl(bdwd.getUrl());


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = PreViewActivity.this;
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
                builder.title(bdwd.getTitle());
                builder.titleGravity(GravityEnum.CENTER);
                builder.titleColorRes(R.color.colorAccent);
                builder.content("本书需消耗您的" + bdwd.getPrize() + "个下载豆");
                builder.items("购买", "取消");
                builder.itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        String textStr = text.toString();
                        if (textStr.equals("取消")) {

                        } else if (textStr.equals("购买")) {
                            downloadWD();
                        }
                    }
                });
                builder.show();
            }
        });


        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collect_image.getTag() != null) {
                    ToastUtil.showSuccess(PreViewActivity.this, "已经在购物车咯~");
                    return;
                }
                MyApplication application = (MyApplication) getApplication();
                final User user = application.getUser();
                if (user == null) {
                    ToastUtil.showError(PreViewActivity.this, getResources().getString(R.string.login_tip));
                    Util.gotoLoginActivity(PreViewActivity.this, LoginActivity.class);
                    return;
                }
                String title = bdwd.getTitle();
                if (title == null) {
                    title = preview_wd_web.getTitle();
                }
                final String url = preview_wd_web.getUrl();
                String id;
                try {
                    id = Util.getDDProductId(url, "pid");
                } catch (MalformedURLException e) {
                    id = null;
                }
                final String finalTitle = title;
                final String finalId = id;
                preview_wd_web.evaluateJavascript("$('#slider img').get(0).src", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        if (s == null) {
                            ToastUtil.showError(PreViewActivity.this, "收藏失败");
                            return;
                        }

                        String pic = s.replace("\"", "");
                        final Collect collect = new Collect(user.getEmail(), finalTitle, url, finalId, pic, 1, 1);
                        collect.save(PreViewActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                ToastUtil.showSuccess(PreViewActivity.this, "加入成功");
                                collect_image.setImageResource(R.drawable.che);
                                collect_image.setTag("collect");
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                if (s.contains(getResources().getString(R.string.unique))) {
                                    ToastUtil.showSuccess(PreViewActivity.this, "已经在购物车咯~");
                                    collect_image.setImageResource(R.drawable.che);
                                    collect_image.setTag("collect");
                                } else if (s.contains(getResources().getString(R.string.nonetwork))) {
                                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                                } else {
                                    ToastUtil.showError(PreViewActivity.this, "加入失败,原因:" + s);
                                }
                            }
                        });
                    }
                });
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showLocationShare(PreViewActivity.this, bdwd.getTitle(), bdwd.getUrl());
            }
        });

        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showLocationShare(PreViewActivity.this, bdwd.getTitle(), bdwd.getUrl());
            }
        });
    }

    public void downloadWD() {
        MyApplication application = (MyApplication) getApplication();
        final User user = application.getUser();
        if (user == null) {
            ToastUtil.showError(PreViewActivity.this, getResources().getString(R.string.login_tip));
            Util.gotoLoginActivity(PreViewActivity.this, LoginActivity.class);
            return;
        }
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("email", user.getEmail());
        query.setLimit(1);
        query.findObjects(PreViewActivity.this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list.isEmpty()) {
                    ToastUtil.showError(PreViewActivity.this, "账号不存在");
                } else {
                    User userTmp = list.get(0);
                    user.set(userTmp);
                    if (user.getCount() <= 0 && !"admin".equals(user.getLevel())) {
                        ToastUtil.showError(PreViewActivity.this, "您的下载豆已用完");
                    } else {
                        user.setCount(user.getCount() - 1);
                        user.setUsecount(user.getUsecount() + 1);
                        user.update(PreViewActivity.this);
                        downloadRecord();
                        ToastUtil.showError(PreViewActivity.this, "您已购买" + bdwd.getTitle() + ",订单已生成");
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if (s.contains(getResources().getString(R.string.nonetwork))) {
                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                } else {
                    ToastUtil.showError(getApplicationContext(), "信息查询出错,原因:" + s);
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
