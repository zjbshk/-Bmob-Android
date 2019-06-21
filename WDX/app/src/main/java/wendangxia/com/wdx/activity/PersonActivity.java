package wendangxia.com.wdx.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wildma.pictureselector.PictureSelector;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class PersonActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private User user;
    private ImageView background_image;
    private EditText person_username;
    private TextView person_email;
    private TextView person_level;
    private TextView person_download_dou;
    private TextView person_usecount;
    private TextView person_address;
    private TextView person_createdAt;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initView();
    }

    private void initView() {
        final MyApplication application = (MyApplication) getApplication();
        user = application.getUser();
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        background_image = (ImageView) findViewById(R.id.background_image);
        /**
         * 初始化content_person
         */

        person_username = (EditText) findViewById(R.id.person_username);
        person_email = (TextView) findViewById(R.id.person_email);
        person_level = (TextView) findViewById(R.id.person_level);
        person_download_dou = (TextView) findViewById(R.id.person_download_dou);
        person_usecount = (TextView) findViewById(R.id.person_usecount);
        person_createdAt = (TextView) findViewById(R.id.person_createdAt);
        logout = (Button) findViewById(R.id.logout);
        person_address = (EditText) findViewById(R.id.person_address);


        person_username.setText(user.getUsername());
        person_address.setText(user.getAddress());
        person_email.setText(user.getEmail());
        person_level.setText(user.getLevel());
        person_download_dou.setText(user.getCount() + "");
        person_usecount.setText(user.getUsecount() + "");
        person_createdAt.setText(user.getCreatedAt());
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.saveKeyValue(PersonActivity.this, "userinfo", "md5", "nouser");
                application.setUser(null);
                onBackPressed();
            }
        });
        /**
         * 设置标题栏
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar toolbarTemp = getSupportActionBar();
        toolbarTemp.setDisplayHomeAsUpEnabled(true);
        toolbarTemp.setHomeAsUpIndicator(R.drawable.back);

        setTitle(user.getUsername());
        if (user.getUrl() != null && user.getUrl().length() != 0) {
            Glide.with(this)
                    .load(user.getUrl())
                    .into(profile_image);
        }
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector
                        .create(PersonActivity.this, PictureSelector.SELECT_REQUEST_CODE)
                        .selectPicture(true, 200, 200, 1, 1);
            }
        });
        String bgUrl = Util.getKeyValue(getApplicationContext(), "initdata", "background", "no");
        if (!bgUrl.equals("no")) {
            System.out.println(bgUrl);
            Glide.with(this).load(bgUrl).into(background_image);

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View viewTmp = view;
                String text = person_username.getText().toString();
                String address = person_address.getText().toString();
                if (text.trim().length() == 0 || address.trim().length() == 0) {
                    ToastUtil.showError(PersonActivity.this, "信息不能为空哦~");
                } else if (text.equals(user.getUsername()) && address.equals(user.getUsername())) {
                    ToastUtil.showError(PersonActivity.this, "没有修改,可以不用保存哦~");
                } else {
                    user.setAddress(address);
                    user.setUsername(text);
                    user.update(PersonActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            PersonActivity.this.setTitle(user.getUsername());
                            ToastUtil.showError(PersonActivity.this, "修改成功啦~");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (s.contains(getResources().getString(R.string.nonetwork))) {
                                ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                            } else {
                                System.out.println(s);
                                ToastUtil.showError(PersonActivity.this, "保存失败啦~,原因:" + s);
                            }
                        }
                    });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                String picturePath = data.getStringExtra(PictureSelector.PICTURE_PATH);
                File image = new File(picturePath);
                final BmobFile bmobFile = new BmobFile(image);
                bmobFile.upload(PersonActivity.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        String getFileUrl = bmobFile.getFileUrl(PersonActivity.this);
                        user.setUrl(getFileUrl);
                        user.update(PersonActivity.this);
                        Glide.with(PersonActivity.this).load(getFileUrl).into(profile_image);
                        ToastUtil.showSuccess(PersonActivity.this, "图片修改成功啦~");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        if (s.contains(getResources().getString(R.string.nonetwork))) {
                            ToastUtil.showError(PersonActivity.this, "无网络连接哦~");
                        } else {
                            ToastUtil.showError(PersonActivity.this, "上传失败,原因:" + s);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
            }
        }
    }
}
