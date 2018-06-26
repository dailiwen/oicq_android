package com.example.dai.oicq_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                new Thread(searchFriendRun).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                searchAdapter.notifyDataSetChanged();
            }
        });

        searchAdapter.setOnItemClickListener(new SearchAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                new Thread(addFriendRun).start();
            }
        });
    }

    Runnable searchFriendRun = new Runnable(){
        @Override
        public void run() {
            ApplicationUtil appUtil = (ApplicationUtil) AddActivity.this.getApplication();
            try {
                if (appUtil.getSocket() == null) {
                    appUtil.init();
                }
                Socket socket = appUtil.getSocket();
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
                    List<Map<String, Object>> maps = (List<Map<String, Object>>) JacksonUtil.jsonToMap(receive).get("result");
                    for (int i = 0; i < maps.size(); i++) {
                        Account account = new Account();
                        account.setId((String) maps.get(i).get("id"));
                        account.setLoginName((String) maps.get(i).get("login_name"));
                        accounts.add(account);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "查无此人", Toast.LENGTH_SHORT).show();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
                Socket socket = appUtil.getSocket();
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
//                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
//                    Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
}
