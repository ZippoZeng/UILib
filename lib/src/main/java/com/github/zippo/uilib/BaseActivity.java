package com.github.zippo.uilib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Zippo on 2018/2/23.
 * Date: 2018/2/23
 * Time: 11:04:19
 */

public abstract class BaseActivity extends AppCompatActivity {




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == bindView()) {
            setContentView(getContentViewId());
        } else {
            setContentView(bindView());
        }
    }

    public abstract int getLayoutId();

    /**
     * 初始化变量：例如类文件中的成员变量，获取从别的组件传过来的数据
     *
     * @param args Intent传过来的数据{@link AppCompatActivity#getIntent()}
     */
    protected abstract void initVariables(Bundle args);

    /**
     * 与{@link BaseActivity#getContentViewId()}两个方法为互斥，非必须实现
     *
     * @return 要显示的view
     */
    protected View bindView() {
        return null;
    }

    //布局文件ID
    protected abstract int getContentViewId();

    protected abstract void initViews();

    /**
     * 初始化各种事件：例如点击事件、选中事件
     */
    protected abstract void initEvents();

    /**
     * 对数据进行初始化：例如对数组的操作等等
     */
    protected void initData(Bundle savedInstanceState) {

    }
}
