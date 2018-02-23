package com.github.zippo.uilib;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zippo on 2018/2/23.
 * Date: 2018/2/23
 * Time: 11:04:25
 */

public abstract class BaseFragment extends Fragment {

    public final String TAG = getClassName();
    protected BaseActivity mActivity;

    /**
     * 获取子类的类名
     */
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (null == bindView(inflater, container)) {
            view = inflater.inflate(getLayoutId(), container, false);
        } else {
            view = bindView(inflater, container);
        }
        Bundle args = getArguments();
        initVariables(args);
        initViews(view);
        initEvents();
        initData();
        return view;
    }

    /**
     * 与getLayoutId两个方法为互斥，非必须实现
     *
     * @param inflater  {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}参数的inflater
     * @param container {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}参数的container
     * @return 要显示的view
     */
    protected View bindView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    /**
     * 获取xml布局文件的id
     *
     * @return 布局文件的id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化变量：例如类文件中的成员变量，获取从别的组件传过来的数据
     *
     * @param args Intent传过来的数据{@link Fragment#getArguments()}
     */
    protected abstract void initVariables(Bundle args);

    /**
     * 对view进行初始化，例如{@link View#findViewById(int)}或设置view的一些参数
     */
    protected abstract void initViews(View pRootView);

    /**
     * 初始化各种事件：例如点击事件{@link View#setOnClickListener(View.OnClickListener)}、选中事件
     */
    protected abstract void initEvents();

    /**
     * 对数据进行初始化：例如对数组的操作等等
     */
    protected void initData() {

    }

    /**
     * 代替查找findviewbyid
     *
     * @param pResId resid
     * @param <T>    View的泛型
     * @return view
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public final <T extends View> T $(View parentView, int pResId) {
        try {
            return (T) parentView.findViewById(pResId);
        } catch (ClassCastException e) {
            Log.e(TAG, "Could not cast View to concrete class.", e);
            throw e;
        }
    }
}
