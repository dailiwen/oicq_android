package com.example.dai.oicq_android;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.oicq_android.adapter.IndicatorExpandableListAdapter;
import com.example.dai.oicq_android.entity.Account;
import com.example.dai.oicq_android.util.ApplicationUtil;
import com.example.dai.oicq_android.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dai.oicq_android.Constant.ACCOUNT;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.register_name)
    EditText registerName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.back_btn)
    Button backBtn;

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(registerName.getText()) || "".equals(confirmPassword.getText()) || "".equals(password.getText())) {
                    showToast("请将信息填完整");
                } else if (confirmPassword.getText().equals(password.getText())) {
                    showToast("两次输入密码不一致");
                } else {
                    new Thread(registerRun).start();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    registerName.clearFocus();//失去焦点
                    password.requestFocus();//获取焦点
                }
                return false;
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    password.clearFocus();//失去焦点
                    confirmPassword.requestFocus();//获取焦点
                }
                return false;
            }
        });

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if ("".equals(registerName.getText()) || "".equals(confirmPassword.getText()) || "".equals(password.getText())) {
                        showToast("请将信息填完整");
                    } else if (confirmPassword.getText().equals(password.getText())) {
                        showToast("两次输入密码不一致");
                    } else {
                        new Thread(registerRun).start();
                    }
                }
                return false;
            }
        });
    }

    Runnable registerRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) RegisterActivity.this.getApplication();
            try {
                if (appUtil.getSocket() == null) {
                    appUtil.init();
                }
                socket = appUtil.getSocket();
                bufferedReader = appUtil.getBufferedReader();
                printWriter = appUtil.getPrintWriter();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            Map<String, String> result = new HashMap<>();
            result.put("type", "register");
            result.put("register_name", registerName.getText().toString());
            result.put("password", password.getText().toString());

            try {
                //发送数据
                String str = JacksonUtil.objectToJson(result);
                printWriter.println(str);
                printWriter.flush();
                Log.d("dailiwen", "result ===> " + str);

                String receive = bufferedReader.readLine();

                if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    showToast("注册成功");
                    finish();
                    break;
                }
                case 2: {
                    showToast("该用户名已被占用");
                    break;
                }
            }
        };
    };
}
