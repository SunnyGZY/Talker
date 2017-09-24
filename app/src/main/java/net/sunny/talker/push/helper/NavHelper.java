package net.sunny.talker.push.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * Created by Sunny on 2017/5/17.
 * Email：670453367@qq.com
 * Description: 完成对Fragment的调度与重用问题
 * 达到最优的Fragment切换
 */

public class NavHelper<T> {
    // 所有Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();

    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    // 当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager,
                     OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 添加Tab
     *
     * @param menuId Tab对应的菜单Id
     * @param tab    Tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;
    }

    // 获取当前显示的Tab
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    private OnTabChangedListener<T> listener;

    /**
     * 执行点击菜单的操作
     *
     * @param position 菜单的Id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenuPosition(int position) {
        Tab<T> tab = tabs.valueAt(position);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    /**
     * 真实的Tab选择操作
     *
     * @param tab
     */
    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                notifyTabReselect(tab);
                return;
            }
        }
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (oldTab != null) {
            if (oldTab.fragment != null) {
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null) {
            if (newTab.fragment == null) {
                Fragment fragment = Fragment.instantiate(context, newTab.clx.getName(), null);
                newTab.fragment = fragment;
                ft.add(containerId, fragment, newTab.clx.getName());
            } else {
                ft.attach(newTab.fragment);
            }
        }
        ft.commit();
        notifyTabSelect(newTab, oldTab);
    }

    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab) {
        // TODO 二次点击Tab刷新操作
    }

    public static class Tab<T> {

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        // Fragment对应的Class信息
        Class<?> clx;
        //额外的字段,用户自己设定需要使用
        public T extra;
        //内部缓存对应的Fragment
        Fragment fragment;
    }

    public interface OnTabChangedListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }
}
