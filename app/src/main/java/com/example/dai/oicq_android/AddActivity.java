package com.example.dai.oicq_android;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.oicq_android.adapter.IndicatorExpandableListAdapter;
import com.example.dai.oicq_android.adapter.SearchAdapter;
import com.example.dai.oicq_android.entity.Account;
import com.example.dai.oicq_android.util.ApplicationUtil;
import com.example.dai.oicq_android.util.JacksonUtil;

import java.io.BufferedReader;
import java.io.IOException;
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

public class AddActivity extends BaseActivity {

    @BindView(R.id.search_friend)
    EditText searchFriend;

    @BindView(R.id.search_btn)
    Button searchBtn;

    @BindView(R.id.search_list)
    RecyclerView searchList;

    SearchAdapter searchAdapter;

    List<Account> accounts = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        //RecyclerView初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchList.setLayoutManager(layoutManager);
        searchAdapter = new SearchAdapter(accounts);
        searchList.setAdapter(searchAdapter);
    }

    @Override
    public void initListener() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchFriend.getText().toString().equals(ACCOUNT.getLoginName())) {
                    new Thread(searchFriendRun).start();
                } else {
                    showToast("无法添加自己为好友");
                }
            }
        });

        searchAdapter.setOnItemClickListener(new SearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                new Thread(addFriendRun).start();
            }
        });

        searchFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new Thread(searchFriendRun).start();
                }
                return false;
            }
        });
    }

    Runnable searchFriendRun = new Runnable(){
        @Override
        public void run() {
            accounts.clear();
            ApplicationUtil appUtil = (ApplicationUtil) AddActivity.this.getApplication();
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
            result.put("type", "search_friend");
            result.put("login_name", searchFriend.getText().toString());

            if (!searchFriend.getText().toString().equals(ACCOUNT.getLoginName())) {
                try {
                    //发送数据
                    String str = JacksonUtil.objectToJson(result);
                    printWriter.println(str);
                    printWriter.flush();
                    Log.d("dailiwen", "result ===> " + str);

                    String receive = bufferedReader.readLine();

                    if ((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                        //Intent intent = new Intent();
                        //intent.setClass(LoginActivity.this, MainActivity.class);
                        List<Map<String, Object>> maps = (List<Map<String, Object>>) JacksonUtil.jsonToMap(receive).get("result");
                        for (int i = 0; i < maps.size(); i++) {
                            Account account = new Account();
                            account.setId((String) maps.get(i).get("id"));
                            account.setLoginName((String) maps.get(i).get("login_name"));
                            accounts.add(account);
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable addFriendRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) AddActivity.this.getApplication();
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
            result.put("type", "add_friend");
            result.put("self_account_id", ACCOUNT.getId());
            result.put("friend_account_id", accounts.get(0).getId());

            try {
                //发送数据
                String str = JacksonUtil.objectToJson(result);
                printWriter.println(str);
                printWriter.flush();
                Log.d("dailiwen", "result ===> " + str);

                String receive = bufferedReader.readLine();

                if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } else if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 1) {
                    Message msg = new Message();
                    msg.what = 3;
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
                case 0: {
                    searchAdapter.notifyDataSetChanged();
                    break;
                }
                case 1: {
                    showToast("查无此人");
                    break;
                }
                case 2: {
                    showToast("添加成功");
                    finish();
                    break;
                }
                case 3: {
                    showToast("您和ta已经是好友了");
                    break;
                }
            }
        };
    };
}
