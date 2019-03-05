package net.sunny.talker.common.widget.recycler;

/**
 * Created by Sunny on 2017/5/14.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public interface AdapterCallback<Data> {

    void update(Data data, BaseRecyclerAdapter.BaseViewHolder<Data> holder);
}
