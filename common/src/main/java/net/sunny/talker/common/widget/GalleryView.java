package net.sunny.talker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.sunny.talker.common.R;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryView extends RecyclerView {

    private static final int LOADER_ID = 0x0100;
    private int maxImageCount = 3;
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;
    private LoaderCallback mLoaderCallback = new LoaderCallback();
    private Adapter mAdapter = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private SelectedChangeListener mListener;

    public void setMaxImageCount(int maxImageCount) {
        this.maxImageCount = maxImageCount;
    }

    public GalleryView(Context context) {
        super(context);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(mAdapter);
        mAdapter.setListener(new BaseRecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, Image image) {
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }

    /**
     * 初始化方法
     *
     * @param loaderManager
     * @return
     */
    public int setup(LoaderManager loaderManager, SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, mLoaderCallback);
        return LOADER_ID;
    }

    /**
     * cell点击的具体逻辑
     *
     * @param image Image
     * @return true代表数据做了更改, 需要刷新, 反之不刷新
     */
    private boolean onItemSelectClick(Image image) {
        boolean notifyRefresh; // 是否需要刷新
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
            image.isSelect = false;
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= maxImageCount) {
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                str = String.format(str, maxImageCount);
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }

        if (notifyRefresh) {
            notifySelectChanged();
        }
        return true;
    }

    /**
     * 得到选中的图片所有地址
     *
     * @return 返回一个数组
     */
    public String[] getSelectedPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    public void clear() {
        for (Image image : mSelectedImages) {
            image.isSelect = false;
        }
        mSelectedImages.clear();
        mAdapter.notifyDataSetChanged();

        notifySelectChanged();
    }

    private void notifySelectChanged() {
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanged(mSelectedImages.size());
        }
    }

    private void updateSource(List<Image> images) {
        mAdapter.add(images);
    }

    /**
     * 用于实际的数据加载的Loader
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // 创建Loader时调用
            if (id == LOADER_ID) {
                return new CursorLoader(getContext(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // 当loader加载完成是调用
            List<Image> images = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();

                    int indexId = data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]);
                    int indexPath = data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]);
                    int indexDate = data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]);

                    do {
                        int id = data.getInt(indexId);
                        String path = data.getString(indexPath);
                        long dataTime = data.getLong(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            continue;
                        }

                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.date = dataTime;
                        images.add(image);

                    } while (data.moveToNext());
                }
            }
            updateSource(images);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // 当Loader销毁或者重置时调用
            updateSource(null);
        }
    }

    private static class Image {
        int id;
        String path;
        long date;
        boolean isSelect;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return path != null ? path.equals(image.path) : image.path == null;

        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }

    private class Adapter extends BaseRecyclerAdapter<Image> {

        @Override
        protected int getItemView(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected BaseViewHolder<Image> onCreateViewHolder(View root, int viewId) {
            return new GalleryView.ViewHolder(root);
        }
    }

    private class ViewHolder extends BaseRecyclerAdapter.BaseViewHolder<Image> {

        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            mPic = (ImageView) itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = (CheckBox) itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(mPic);
            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }

    public interface SelectedChangeListener {
        void onSelectedCountChanged(int count);
    }
}