package wendangxia.com.wdx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import wendangxia.com.wdx.R;
import wendangxia.com.wdx.adapt.MySearchRecyclerViewAdapt;
import wendangxia.com.wdx.bean.Bdwd;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class SearchActivity extends AppCompatActivity {

    private EditText search_edit;
    private ImageView search_submit;
    private RecyclerView search_recycler;
    private AVLoadingIndicatorView avloading;
    private List<Bdwd> bdwdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        search_edit = (EditText) findViewById(R.id.search_edit);
        avloading = (AVLoadingIndicatorView) findViewById(R.id.avloading);
        search_recycler = (RecyclerView) findViewById(R.id.search_recycler);
        search_recycler.setLayoutManager(new LinearLayoutManager(this));
        MySearchRecyclerViewAdapt adapt = new MySearchRecyclerViewAdapt(bdwdList, this, R.layout.recycle_item_search_activity);
        search_recycler.setAdapter(adapt);
        search_submit = (ImageView) findViewById(R.id.search_submit);
        search_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = search_edit.getText().toString();
                if (url.trim().length() == 0) {
                    ToastUtil.showError(SearchActivity.this, "链接不能为空");
                } else if (!url.contains("baidu.com")) {
                    ToastUtil.showError(SearchActivity.this, "暂时只支持百度文库文档哦");
                } else {
                    if (!Util.isUrl(url)) {
                        ToastUtil.showError(SearchActivity.this, "链接格式有误");
                    } else {
                        avloading.setVisibility(View.VISIBLE);
                        avloading.show();
                        JsoupUrl(url);
                    }
                }
            }
        });
    }

    private void JsoupUrl(String finalurl) {
        String url = null;
        try {
            url = getResources().getString(R.string.baseurl) + Util.getBaiduDocId(finalurl);
        } catch (MalformedURLException e) {
            url = finalurl;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showError(SearchActivity.this, "搜索文档失败");
                        avloading.setVisibility(View.GONE);
                        avloading.hide();
                    }
                });
                call.cancel();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = response.body().byteStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "GB2312"));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                final String sub = Util.getSubContent(sb.toString(), "WkInfo.DocInfo = {", "};");
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avloading.setVisibility(View.GONE);
                        avloading.hide();
                        if (sub == null) {
                            ToastUtil.showError(SearchActivity.this, "信息获取失败啦");
                        } else {
                            Bdwd bdwd = Util.getDocInfo(sub);
                            if (bdwdList.contains(bdwd)) {
                                ToastUtil.showError(SearchActivity.this, "信息已在列表");
                            } else {
                                bdwdList.add(0, bdwd);
                                search_recycler.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }
                });
                response.body().close();
                call.cancel();
            }
        });
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
