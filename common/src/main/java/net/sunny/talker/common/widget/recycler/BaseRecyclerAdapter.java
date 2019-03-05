package net.sunny.talker.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sunny.talker.common.R;
import net.sunny.talker.view.okrecycler.OkRecycleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 封装的RecycleView适配器
 *
 * @author gaozongyang
 * @date 2017/5/14
 */
public abstract class BaseRecyclerAdapter<Data>
        extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<Data>> implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {

    final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    public BaseRecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    public BaseRecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }

    public BaseRecyclerAdapter() {
        this(new ArrayList<Data>(), null);
    }

    @Override
    public int getItemCount() {
        if (this.mListener != null) {
            this.mListener.getItemCount(mDataList.size());
        }
        return mDataList.size();
    }

    public List<Data> getItems() {
        return mDataList;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemView(position, mDataList.get(position));
    }

    /**
     * 获取item布局的id
     *
     * @param position 位置
     * @param data     数据
     * @return item布局的id
     */
    @LayoutRes
    protected abstract int getItemView(int position, Data data);

    @Override
    public BaseViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewId, parent, false);
        BaseViewHolder<Data> holder = onCreateViewHolder(root, viewId);

        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        root.setTag(R.id.tag_recycler_holder, holder);

        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    /**
     * 创建ViewHolder
     *
     * @param root     根布局
     * @param viewType 数据类别
     * @return BaseViewHolder
     */
    protected abstract BaseViewHolder<Data> onCreateViewHolder(View root, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder<Data> holder, int position) {
        Data data = mDataList.get(position);
        holder.bind(data);
    }

    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    public void addFromHead(OkRecycleView okRecycleView, Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            mDataList.addAll(0, Arrays.asList(dataList));

            notifyItemRangeChanged(okRecycleView.getHeadersCount(), mDataList.size());
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public void update(Data data, BaseViewHolder<Data> holder) {
        int pos = holder.getAdapterPosition();
        if (pos >= 0) {
            mDataList.remove(pos);
            mDataList.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View v) {
        if (this.mListener != null) {
            BaseViewHolder viewHolder = (BaseViewHolder) v.getTag(R.id.tag_recycler_holder);
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (this.mListener != null) {
            BaseViewHolder viewHolder = (BaseViewHolder) v.getTag(R.id.tag_recycler_holder);
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemLongClick(viewHolder, mDataList.get(pos));
        }

        return false;
    }

    public static abstract class BaseViewHolder<Data> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback<Data> callback;
        protected Data mData;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 绑定数据
         *
         * @param data 数据
         */
        protected abstract void onBind(Data data);

        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }

    public void setListener(AdapterListener<Data> adapterListener) {
        this.mListener = adapterListener;
    }

    protected interface AdapterListener<Data> {
        /**
         * 点击事件
         *
         * @param holder BaseViewHolder
         * @param data   数据
         */
        void onItemClick(BaseViewHolder holder, Data data);

        /**
         * 长按点击事件
         *
         * @param holder BaseViewHolder
         * @param data   数据
         */
        void onItemLongClick(BaseViewHolder holder, Data data);

        /**
         * 回调item的数量，用于RecyclerView滚动
         *
         * @param count 当前item的数量
         */
        void getItemCount(int count);
    }

    public static class AdapterListenerImpl<Data> implements AdapterListener<Data> {

        @Override
        public void onItemClick(BaseViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(BaseViewHolder holder, Data data) {

        }

        @Override
        public void getItemCount(int count) {

        }
    }
}
