package wendangxia.com.wdx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import wendangxia.com.wdx.R;
import wendangxia.com.wdx.application.MyApplication;
import wendangxia.com.wdx.bean.User;
import wendangxia.com.wdx.util.ToastUtil;
import wendangxia.com.wdx.util.Util;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText affirm_password;
    private TextView register;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.register);

        affirm_password = (EditText) findViewById(R.id.affirm_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                String affirm_passwordStr = affirm_password.getText().toString().trim();
                if (affirm_password.getVisibility() != View.VISIBLE) {
                    if (checkUserPassword(email, password)) {
                        loginBmob(email, password);
                    }
                } else {
                    if (checkUserPassword(email, password) && checkUserPasswordForR(email, password, affirm_passwordStr)) {
                        registerBmob(email, password);
                    }
                }
            }
        });
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (affirm_password.getVisibility() != View.VISIBLE) {
                    mEmailSignInButton.setText("注册");
                    affirm_password.setVisibility(View.VISIBLE);
                    register.setText("去登录");
                    register.setGravity(Gravity.START);
                } else {
                    mEmailSignInButton.setText("登录");
                    affirm_password.setVisibility(View.GONE);
                    register.setText("去注册");
                    register.setGravity(Gravity.END);
                }
            }
        });
    }

    private void registerBmob(final String email, final String password) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("email", email);
        query.count(LoginActivity.this, User.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i == 0) {
                    User user = new User();
                    user.setEmail(email);
                    user.setPassword(password);
                    String md5 = Util.MD5(email + ":" + password);
                    user.setMd5(md5);
                    user.setCount(0);
                    user.setUsecount(0);
                    user.setLevel("初级小白");
                    user.setUsername("未命名");
                    user.save(LoginActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            ToastUtil.showError(getApplicationContext(), "注册成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (s.contains(getResources().getString(R.string.nonetwork))) {
                                ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                            } else {
                                ToastUtil.showError(LoginActivity.this, "注册失败,原因:" + s);
                            }
                        }
                    });
                } else {
                    ToastUtil.showError(LoginActivity.this, "该邮箱已被注册");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (s.contains(getResources().getString(R.string.nonetwork))) {
                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                } else {
                    ToastUtil.showError(LoginActivity.this, "注册失败,原因:" + s);
                }
            }
        });
    }

    private boolean checkUserPasswordForR(String email, String password, String affirm_passwordStr) {
        if (!password.equals(affirm_passwordStr)) {
            ToastUtil.showError(this, "两次密码不一致");
            return false;
        } else if (password.length() < 6) {
            ToastUtil.showError(this, "密码长度太短了");
            return false;
        }
        return true;
    }

    private void loginBmob(String email, String password) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("email", email);
        query.addWhereEqualTo("password", password);
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list.size() == 1) {
                    MyApplication application = (MyApplication) getApplication();
                    application.setUser(list.get(0));
                    User user = application.getUser();
                    ToastUtil.showSuccess(LoginActivity.this, "登录成功");
                    String md5 = Util.MD5(user.getEmail() + ":" + user.getPassword());
                    Util.saveKeyValue(LoginActivity.this, "userinfo", "md5", md5);
                    Util.saveKeyValue(LoginActivity.this, "userinfo", "email", user.getEmail());
                    finish();
                } else {
                    ToastUtil.showError(LoginActivity.this, "账号或密码有误");
                }
            }

            @Override
            public void onError(int i, String s) {
                if (s.contains(getResources().getString(R.string.nonetwork))) {
                    ToastUtil.showError(getApplicationContext(), "无网络连接哦~");
                } else {
                    ToastUtil.showError(LoginActivity.this, "登录失败,原因:" + s);
                }
            }
        });
    }

    private boolean checkUserPassword(String email, String password) {
        if (email.length() == 0) {
            ToastUtil.showError(this, "邮箱不能为空");
            return false;
        } else if (password.length() == 0) {
            ToastUtil.showError(this, "密码不能为空");
            return false;
        } else if (password.length() < 6) {
            ToastUtil.showError(this, "密码长度最短6位,请检查");
            return false;
        } else if (!email.contains("@")) {
            ToastUtil.showError(this, "邮箱格式有误");
            return false;
        }
        return true;
    }

}

