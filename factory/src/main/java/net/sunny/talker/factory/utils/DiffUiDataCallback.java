package net.sunny.talker.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by Sunny on 2017/6/7.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {

    private List<T> mOldData, mNewList;

    public DiffUiDataCallback(List<T> mOldData, List<T> mNewList) {
        this.mOldData = mOldData;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        // 旧的数据大小
        return mOldData.size();
    }

    @Override
    public int getNewListSize() {
        // 新的数据大小
        return mNewList.size();
    }

    // 两个类是否就是同一个东西
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldData.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);

        return beanNew.isSame(beanOld);
    }

    // 在经过相等判断后，进一步判断是否有数据更改
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldData.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);

        return beanNew.isUiContentSame(beanOld);
    }

    public interface UiDataDiffer<T> {
        boolean isSame(T old);

        boolean isUiContentSame(T old);
    }
}
