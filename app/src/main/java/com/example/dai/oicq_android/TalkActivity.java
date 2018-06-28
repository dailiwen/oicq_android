package com.example.dai.oicq_android;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.oicq_android.adapter.TalkAdapter;
import com.example.dai.oicq_android.entity.Talk;
import com.example.dai.oicq_android.util.ApplicationUtil;
import com.example.dai.oicq_android.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dai.oicq_android.Constant.ACCOUNT;

public class TalkActivity extends BaseActivity {
    /**
     * 退出登录按钮
     */
    @BindView(R.id.back_btn)
    LinearLayout backBtn;

    @BindView(R.id.friend_name)
    TextView friendName;

    @BindView(R.id.recycler_talk)
    RecyclerView recyclerTalk;

    @BindView(R.id.send_edit)
    EditText sendEdit;

    @BindView(R.id.send_btn)
    Button sendBtn;

    private TalkAdapter talkAdapter;

    private List<Talk> massage = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitle(true);
        setContentView(R.layout.activity_talk);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
        new Thread(reciveMassageRun).start();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        friendName.setText(getIntent().getExtras().getString("name"));

        //RecyclerView初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerTalk.setLayoutManager(layoutManager);
        talkAdapter = new TalkAdapter(massage);
        recyclerTalk.setAdapter(talkAdapter);
        talkAdapter.notifyDataSetChanged();

        //发送按钮初始化
        sendBtn.setEnabled(Boolean.FALSE);
    }

    @Override
    public void initListener() {
        //输入框事件
        sendEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sendEdit.getText().length() == 0) {
                    sendBtn.setEnabled(Boolean.FALSE);
                    sendBtn.setBackgroundResource(R.drawable.send_btn);

                } else {
                    sendBtn.setEnabled(Boolean.TRUE);
                    sendBtn.setBackgroundResource(R.drawable.send_btn_1);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //发送按钮事件
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(sendMassageRun).start();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    new Thread(sendMassageRun).start();
                }
                return false;
            }
        });

    }

    Runnable sendMassageRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) TalkActivity.this.getApplication();
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
            result.put("type", "send_massage");
            result.put("friend_id", getIntent().getExtras().getString("id"));
            result.put("message", sendEdit.getText().toString());

            //发送数据
            String str = JacksonUtil.objectToJson(result);
            printWriter.println(str);
            printWriter.flush();
            Talk talk = new Talk();
            talk.setType(2);
            talk.setMessage(sendEdit.getText().toString());
            massage.add(talk);
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
            Log.d("dailiwen", "result ===> " + str);
        }
    };

    Runnable reciveMassageRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) TalkActivity.this.getApplication();
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
            while (true) {
                try {
                    boolean online = true;
                    while (online) {
                        String receive = bufferedReader.readLine();
                        if (receive != null) {
                            if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                                Talk talk = new Talk();
                                Map<String, Object> maps = (Map<String, Object>) JacksonUtil.jsonToMap(receive).get("result");
                                talk.setType(1);
                                talk.setMessage((String) maps.get("message"));
                                massage.add(talk);
                                Message msg = new Message();
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            }
                        } else {
                            bufferedReader.close();
                            bufferedReader = appUtil.getBufferedReader();
                        }
                    }
                    printWriter.close();
                    bufferedReader.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    talkAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    talkAdapter.notifyDataSetChanged();
                    sendEdit.setText("");
                    break;
            }
        };
    };
}
