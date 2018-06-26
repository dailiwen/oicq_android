package com.example.dai.oicq_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dai.oicq_android.adapter.IndicatorExpandableListAdapter;
import com.example.dai.oicq_android.entity.Account;
import com.example.dai.oicq_android.entity.Talk;
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
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import static com.example.dai.oicq_android.Constant.ACCOUNT;

public class UserActivity extends BaseActivity {
    /**
     * 好友下拉单
     */
    @BindView(R.id.expand_list)
    ExpandableListView expandableListView;
    IndicatorExpandableListAdapter adapter;
    /**
     * 下拉刷新
     */
    @BindView(R.id.user_refresh)
    PtrFrameLayout userRefresh;
    /**
     * 新朋友按钮
     */
    @BindView(R.id.new_friend)
    TextView newFriend;

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private List<String> fatherList = new ArrayList<>();
    private List<String> onlineList = new ArrayList<>();
    private List<String> offlineList = new ArrayList<>();
    private List<String> onlineidList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        initData();
        adapter = new IndicatorExpandableListAdapter(fatherList, onlineList, offlineList);
        initView();
        initListener();
    }

    @Override
    public void initData() {
        fatherList.add("未在线");
        fatherList.add("已在线");
        new Thread(friendListRun).start();
    }

    @Override
    public void initView() {
        //设置ExpandableListView的adapter
        expandableListView.setAdapter(adapter);
        // 清除默认的 Indicator
        expandableListView.setGroupIndicator(null);

        //下拉刷新配置
        final PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(this);
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, 0);
        // mPtrFrame = (PtrFrameLayout) findViewById(R.id.ptr);
        userRefresh.setHeaderView(header);
        userRefresh.addPtrUIHandler(header);
        userRefresh.disableWhenHorizontalMove(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(friendListRun).start();
        adapter = new IndicatorExpandableListAdapter(fatherList, onlineList, offlineList);
        //设置ExpandableListView的adapter
        expandableListView.setAdapter(adapter);
        // 清除默认的 Indicator
        expandableListView.setGroupIndicator(null);
    }

    @Override
    public void initListener() {
        //新朋友按钮
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddActivity.class);
            }
        });

        //  设置分组项的点击监听事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                boolean groupExpanded = parent.isGroupExpanded(groupPosition);
                adapter.setIndicatorState(groupPosition, groupExpanded);
                // 请务必返回 false，否则分组不会展开
                return false;
            }
        });

        //  设置子选项点击监听事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    showToast(offlineList.get(childPosition) + "未在线");
                } else if (groupPosition == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id",onlineidList.get(childPosition));
                    bundle.putString("name",onlineList.get(childPosition));
                    startActivity(TalkActivity.class, bundle);
                }
                return true;
            }
        });

        //下拉刷新监听
        userRefresh.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(friendListRun).start();
                        adapter = new IndicatorExpandableListAdapter(fatherList, onlineList, offlineList);
                        //设置ExpandableListView的adapter
                        expandableListView.setAdapter(adapter);
                        // 清除默认的 Indicator
                        expandableListView.setGroupIndicator(null);
                    }
                }, 0);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    Runnable friendListRun = new Runnable(){
        @Override
        public void run() {
            offlineList.clear();
            onlineList.clear();
            onlineidList.clear();
            ApplicationUtil appUtil = (ApplicationUtil) UserActivity.this.getApplication();
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
            result.put("type", "get_friend");
            result.put("self_account_id", ACCOUNT.getId());

            try {
                //发送数据
                String str = JacksonUtil.objectToJson(result);
                printWriter.println(str);
                printWriter.flush();
                Log.d("dailiwen", "result ===> " + str);

                String receive = bufferedReader.readLine();

                if((Integer) JacksonUtil.jsonToMap(receive).get("type") == 0) {
                    List<Map<String, Object>> maps = (List<Map<String, Object>>) JacksonUtil.jsonToMap(receive).get("result");
                    for (int i = 0; i < maps.size(); i++) {
                        //未上线好友
                        if ((Integer) maps.get(i).get("stats") == 0) {
                            offlineList.add((String) maps.get(i).get("login_name"));
                        } else if ((Integer) maps.get(i).get("stats") == 1) {
                            onlineList.add((String) maps.get(i).get("login_name"));
                            onlineidList.add((String) maps.get(i).get("id"));
                        }
                    }
                } else {
//                    showToast("123");
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


}
