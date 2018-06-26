package com.example.dai.oicq_android.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.example.dai.oicq_android.R;

/**
 * 自定义加载界面
 * @author dailiwen
 * @date 2018/04/20
 */
public class MyDialog extends ProgressDialog {
    public MyDialog(Context context) {
        super(context);
    }


    public MyDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.progress_dialog);
    }

    @Override
    public void show()
    {
        super.show();
    }
}
