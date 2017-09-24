package net.sunny.talker.push.fragments.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import net.sunny.talker.common.tools.UiTool;
import net.sunny.talker.common.widget.GalleryView;
import net.sunny.talker.push.R;

/**
 * 图片选择Fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleryView.SelectedChangeListener {

    private GalleryView mGallery;
    private OnSelectedListener mListener;
    private int maxImageCount = 1;

    public GalleryFragment setMaxImageCount(int maxImageCount) {
        this.maxImageCount = maxImageCount;
        return this;
    }

    public GalleryFragment() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (maxImageCount != 1)
            setHasOptionsMenu(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (maxImageCount != 1) {
            menu.clear();
            inflater.inflate(R.menu.select_photos, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGallery = (GalleryView) root.findViewById(R.id.galleryView);
        mGallery.setMaxImageCount(maxImageCount);

        if (maxImageCount != 1) {
            AppCompatActivity mAppCompatActivity = (AppCompatActivity) getActivity();
            Toolbar mToolbar = (Toolbar) root.findViewById(R.id.fragment_toolbar);
            mAppCompatActivity.setSupportActionBar(mToolbar);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setup(getLoaderManager(), this);
    }

    @Override
    public void onSelectedCountChanged(int count) {
        if (mListener != null) {
            String[] paths = mGallery.getSelectedPath();
            mListener.onSelectedImageCount(paths.length);

            if (maxImageCount == 1 && count > 0) {
                dismiss();
                mListener.onSelectedImage(paths);
                mListener = null;
            }
        }
    }

    public GalleryFragment setListener(OnSelectedListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ok:
                dismiss();
                if (mListener != null) {
                    String[] paths = mGallery.getSelectedPath();
                    mListener.onSelectedImage(paths);
                    mListener = null;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    interface OnSelectedListener {
        void onSelectedImage(String[] path);

        void onSelectedImageCount(int count);
    }

    public static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final Window window = getWindow();
            if (window == null)
                return;

            // 得到屏幕高度
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            // 得到状态栏的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());

            // 计算dialog的高度并设置
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        }
    }

    public static class GalleryListenerImpl implements OnSelectedListener {

        @Override
        public void onSelectedImage(String[] path) {

        }

        @Override
        public void onSelectedImageCount(int count) {

        }
    }
}
