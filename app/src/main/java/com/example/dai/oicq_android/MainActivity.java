package com.example.dai.oicq_android;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.oicq_android.entity.Account;
import com.example.dai.oicq_android.util.ApplicationUtil;
import com.example.dai.oicq_android.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dai.oicq_android.Constant.ACCOUNT;

public class MainActivity extends BaseActivity {
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.login_name)
    EditText loginName;
    @BindView(R.id.password)
    EditText password;

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(loginRun).start();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
            }
        });

        loginName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    loginName.clearFocus();//失去焦点
                    password.requestFocus();//获取焦点
                }
                return false;
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new Thread(loginRun).start();
                }
                return false;
            }
        });
    }

    Runnable loginRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) MainActivity.this.getApplication();
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
            result.put("type", "login");
            result.put("login_name", loginName.getText().toString());
            result.put("password", password.getText().toString());

            try {
                //发送数据
                String str = JacksonUtil.objectToJson(result);
                printWriter.println(str);
                printWriter.flush();
                Log.d("dailiwen", "result ===> " + str);

                String receive = bufferedReader.readLine();

                if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                    //Intent intent = new Intent();
                    //intent.setClass(LoginActivity.this, MainActivity.class);
                    Map<String, Object> map = (Map<String, Object>) JacksonUtil.jsonToMap(receive).get("result");
                    ACCOUNT = (Account) JacksonUtil.map2Object(map, Account.class);
                    startActivity(UserActivity.class);
                    finish();
                } else {
                    Looper.prepare();
                    showToast("用户或密码输入错误");
                    Looper.loop();

                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}
