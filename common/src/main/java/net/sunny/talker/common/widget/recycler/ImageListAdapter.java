package net.sunny.talker.common.widget.recycler;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.sunny.talker.common.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sunny on 2018/5/31.
 * 动态编辑页面图片选择适配器
 */
public class ImageListAdapter extends RecyclerAdapter<String> {

    private boolean isLock = false;

    private int maxCount; // 最大显示图片数量

    private OnItemClickListener onItemClickListener;

    private List<DeleteClickListener> deleteClickListeners;

    public ImageListAdapter(int maxCount, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.maxCount = maxCount;

        deleteClickListeners = new ArrayList<>();
    }

    /**
     * 添加图片
     *
     * @param imgUri 图片URL
     */
    @Override
    public void add(String... imgUri) {

        if (!hasEmptyView())
            return;

        isLock = false;
        deleteClickListeners.clear();
        if (mDataList.size() - 1 < maxCount) {
            int spareCount = maxCount - (mDataList.size() - 1);
            if (spareCount >= imgUri.length) {
                mDataList.addAll(mDataList.size() - 1, Arrays.asList(imgUri));
            } else {
                String[] tempData = new String[spareCount];
                System.arraycopy(imgUri, 0, tempData, 0, spareCount);
                mDataList.addAll(mDataList.size() - 1, Arrays.asList(tempData));
            }

            if (mDataList.size() > maxCount) {
                mDataList.remove(mDataList.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 删除图片
     *
     * @param position 待删除的图片位置
     */
    private void del(int position) {
        isLock = true;

        mDataList.remove(position);
        if (!hasEmptyView() && mDataList.size() < maxCount) {
            mDataList.add(mDataList.size(), "empty");
        }

        deleteClickListeners.remove(position);
        for (int i = 0; i < deleteClickListeners.size(); i++) {
            deleteClickListeners.get(i).setPosition(i);
        }
        notifyItemRemoved(position);
    }

    /**
     * 判断图片集合中是否存在空数据
     *
     * @return true 存在
     */
    private boolean hasEmptyView() {
        for (String s : mDataList) {
            if ("empty".equals(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected int getItemView(int position, String s) {
        return R.layout.cell_write_track_photo;
    }

    @Override
    protected ViewHolder<String> onCreateViewHolder(View root, int viewType) {
        return new PhotoHolder(root);
    }

    @Override
    public void onBindViewHolder(ViewHolder<String> holder, int position) {
        if (!isLock && !"empty".equals(mDataList.get(position))) {
            PhotoHolder photoHolder = (PhotoHolder) holder;
            DeleteClickListener deleteClickListener = new DeleteClickListener();
            deleteClickListener.setPosition(position);
            photoHolder.setOnDeleteClickListener(deleteClickListener);
            deleteClickListeners.add(deleteClickListener);
        }
        super.onBindViewHolder(holder, position);
    }

    class PhotoHolder extends RecyclerAdapter.ViewHolder<String> {

        private ImageView photo; // 图片展示

        private View delete; // 删除按钮

        private Context context;

        private OnDeleteClickListener onDeleteClickListener;

        private void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
            this.onDeleteClickListener = onDeleteClickListener;
        }

        PhotoHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();

            findView(itemView);
        }

        private void findView(View rootView) {
            photo = (ImageView) rootView.findViewById(R.id.iv_photo);
            delete = rootView.findViewById(R.id.iv_delete);
        }

        @Override
        protected void onBind(String string) {
            if (string.equals("empty")) { // 如果不是照片
                photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                photo.setPadding(56, 56, 56, 56);
                photo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_default_photo));
                delete.setVisibility(View.GONE);

                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onAddItemClick();
                    }
                });
                delete.setOnClickListener(null);
            } else {
                photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photo.setPadding(0, 0, 0, 0);
                Glide.with(photo.getContext())
                        .load(string)
                        .fitCenter()
                        .into(photo);
                delete.setVisibility(View.VISIBLE);

                photo.setOnClickListener(null);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onDeleteClickListener != null)
                            onDeleteClickListener.deleteClick();
                    }
                });
            }
        }
    }

    /**
     * 删除按钮点击事件处理类
     */
    private class DeleteClickListener implements OnDeleteClickListener {

        int position;

        @Override
        public void deleteClick() {
            del(position);
        }

        @Override
        public void setPosition(int position) {
            this.position = position;
        }
    }

    public interface OnItemClickListener {
        void onAddItemClick();
    }

    private interface OnDeleteClickListener {

        void deleteClick();

        void setPosition(int position);
    }
}
