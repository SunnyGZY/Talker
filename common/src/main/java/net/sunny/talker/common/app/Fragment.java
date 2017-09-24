package net.sunny.talker.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sunny.talker.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sunny on 2017/5/14.
 * Email：670453367@qq.com
 * Description: Fragment封装
 */

public abstract class Fragment extends android.support.v4.app.Fragment {

    protected View mRoot;
    protected Unbinder mRootUnBinder;
    protected PlaceHolderView mPlaceHolderView;
    protected boolean mIsFirstInitData = true; // 是否第一次初始化数据

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRoot == null) {
            int layId = getContentLayoutId();
            mRoot = inflater.inflate(layId, container, false);
            initWidget(mRoot);
        } else {
            if (mRoot.getParent() != null) {
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData) {
            mIsFirstInitData = false;
            onFirstInit();
        }
        // 当View创建完成之后
        initData();
    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数正确返回true, 错误返回false
     */
    protected void initArgs(Bundle bundle) {

    }

    /**
     * 得到当前界面的资源ID
     *
     * @return 资源文件ID
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(View root) {
        mRootUnBinder = ButterKnife.bind(this, mRoot);
    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    protected void onFirstInit() {

    }

    /**
     * 返回按键触发时调用
     *
     * @return 返回true代表我已处理返回逻辑, Activity不用自己finish.
     * 返回false代表我没有处理,Activity自己走自己的逻辑.
     */
    public boolean onBackPressed() {
        return false;
    }

    public void setPlaceHolderView(PlaceHolderView placeHolderView) {
        this.mPlaceHolderView = placeHolderView;
    }
}
