package net.sunny.talker.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.sunny.talker.common.R;
import net.sunny.talker.common.app.Application;
import net.sunny.talker.utils.DateTimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


public class GalleryDialog extends Dialog {

    private List<String> imageUrls;
    private int clickPosition;
    private View downLoad;

    /**
     * 指定显示第几张图片
     *
     * @param context   上下文
     * @param imageUrls 图片地址
     * @param position  默认显示第 position 张图片
     */
    public GalleryDialog(Context context, List<String> imageUrls, int position) {
        super(context, R.style.GalleryDialogTheme);
        this.imageUrls = imageUrls;
        this.clickPosition = position;
    }

    public GalleryDialog(Context context, List<String> imageUrls) {
        this(context, imageUrls, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gallery);
        //按空白处不能取消动画  
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);

        PicPagerAdapter mAdapter = new PicPagerAdapter(getContext(), imageUrls);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(clickPosition);

        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        downLoad = findViewById(R.id.iv_download);
        downLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downImage(mViewPager);
            }
        });
    }

    private void downImage(ViewPager mViewPager) {
        int position = mViewPager.getCurrentItem();
        final String url = imageUrls.get(position);

        Glide.with(getContext()).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                try {
                    Date date = new Date();

                    String time = DateTimeUtil.getIntactData(date);
                    // TODO: 17-7-22 不管什么图片资源，都保存成了jpg格式
                    saveFileToSD(time + ".jpg", bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveFileToSD(String filename, byte[] bytes) throws Exception {
        // 如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "talker";
            File dir1 = new File(filePath);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            filename = filePath + "/" + filename;
            FileOutputStream output = new FileOutputStream(filename);
            output.write(bytes);
            output.close();
            Application.showToast("图片已成功保存至：" + filePath);
        } else {
            Application.showToast("SD卡不存在或者不可读写");
        }
    }

    private class PicPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<String> mData;

        PicPagerAdapter(Context context, List<String> list) {
            mContext = context;
            mData = list;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        /**
         * 实例化一个页卡
         */
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new PhotoView(mContext);

            Glide.with(mContext)
                    .load(mData.get(position))
                    .fitCenter()
                    .into(imageView);

            container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            return imageView;
        }

        /**
         * 销毁一个页卡
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * 判断view是否来自对象
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}