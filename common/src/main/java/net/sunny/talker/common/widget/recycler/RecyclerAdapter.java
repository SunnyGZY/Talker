package net.sunny.talker.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sunny.talker.common.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sunny on 2017/5/14.
 * Email：670453367@qq.com
 * Description: 封装的RecycleView适配器
 */

public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>> implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {

    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;
    public boolean hasHeaded = false;
    public boolean hasFooter = false;

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }

    public RecyclerAdapter() {
        this(new ArrayList<Data>(), null);
    }

    @Override
    public int getItemCount() {
        if (this.mListener != null)
            this.mListener.getItemCount(mDataList.size());

        return mDataList.size();
    }

    public List<Data> getItems() {
        return mDataList;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemView(position, mDataList.get(position));
    }

    @LayoutRes
    protected abstract int getItemView(int position, Data data);

    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewId, parent, false);
        ViewHolder<Data> holder = onCreateViewHolder(root, viewId);

        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        root.setTag(R.id.tag_recycler_holder, holder);

        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
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

    public void addFromHead(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            mDataList.addAll(0, Arrays.asList(dataList));
            notifyItemRangeChanged(0, mDataList.size() );
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeChanged(startPos, dataList.size());
        }
    }

    public void addHeader() {
        mDataList.add(0, null);
        hasHeaded = true;
        notifyDataSetChanged();
    }

    public void addFooter() {
        mDataList.add(getItemCount() - 1, null);
        hasFooter = true;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public void update(Data data, ViewHolder<Data> holder) {
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
            ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (this.mListener != null) {
            ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
            int pos = viewHolder.getAdapterPosition();
            this.mListener.onItemLongClick(viewHolder, mDataList.get(pos));
        }

        return false;
    }

    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback<Data> callback;
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

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
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        void onItemLongClick(RecyclerAdapter.ViewHolder holder, Data data);

        // 回调当前消息的数量，用于RecyclerView滚动
        void getItemCount(int count);
    }

    public static class AdapterListenerImpl<Data> implements AdapterListener<Data> {

        @Override
        public void onItemClick(ViewHolder holder, Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, Data data) {

        }

        @Override
        public void getItemCount(int count) {

        }
    }
}
