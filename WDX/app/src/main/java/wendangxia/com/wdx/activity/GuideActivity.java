package wendangxia.com.wdx.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.bean.Preview;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class GuideActivity extends AppCompatActivity {

    private LottieAnimationView animation_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initData();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                GuideActivity.this.startActivity(intent);
                GuideActivity.this.overridePendingTransition(R.anim.in,R.anim.out);
                finish();
            }
        }, 6000);
    }

    private void initData() {
        BmobQuery<Preview> query = new BmobQuery<>();
        List<String> allkeys = Arrays.asList("jscode", "version", "app_guide","background");
        final List<String> parsekeys = Arrays.asList("app_guide");
        query.addWhereContainedIn("key", allkeys);
        query.findObjects(this, new FindListener<Preview>() {
            @Override
            public void onSuccess(List<Preview> list) {
                if (list.isEmpty()) {
                    return;
                }
                List<String> keys = new ArrayList<String>();
                List<Object> values = new ArrayList<Object>();
                for (Preview preview : list) {
                    if (parsekeys.contains(preview.getKey())) {
                        parsePreview(preview);
                    } else {
                        keys.add(preview.getKey());
                        values.add(preview.getValue());
                    }
                }
                Util.saveKeyValue(getApplicationContext(), "initdata", keys, values);
            }

            @Override
            public void onError(int i, String s) {
                System.out.println(s);
            }
        });
    }

    private void parsePreview(Preview preview) {
        if ("app_guide".equals(preview.getKey())) {
            app_guide(preview);
        }
    }

    private void app_guide(Preview preview) {
        String url = Util.getKeyValue(this, "initdata", "app_guide_url", null);
        if (url != null) {
            if (!url.equals(preview.getValue())) {
                Util.downloadAppGuide(preview.getValue(), "books.json", GuideActivity.this);
            }
        } else {
            Util.downloadAppGuide(preview.getValue(), "books.json", GuideActivity.this);
        }
        Util.saveKeyValue(this, "initdata", "app_guide_url", preview.getValue());
    }


    private void initView() {
        animation_view = (LottieAnimationView) findViewById(R.id.animation_view);
        File root = getFilesDir();
        File books = new File(root, "books.json");
        if (books.exists()) {
            try {
                InputStream is = new FileInputStream(books);
                LottieComposition.Factory.fromInputStream(this, is, new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        animation_view.setComposition(composition);
                        animation_view.playAnimation();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            animation_view.setAnimation("books.json");
            animation_view.playAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in,R.anim.out);
    }
}
