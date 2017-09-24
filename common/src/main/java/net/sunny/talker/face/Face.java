package net.sunny.talker.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.sunny.talker.common.R;
import net.sunny.talker.utils.StreamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by sunny on 17-7-16.
 * 表情工具类
 */

public class Face {

    // ArrayMap 更加轻量级
    private static final ArrayMap<String, Bean> FACE_MAP = new ArrayMap<>();
    private static List<FaceTab> FACE_TABS = null;

    private static void init(Context context) {
        if (FACE_TABS == null)
            synchronized (Face.class) {
                if (FACE_TABS == null) {
                    ArrayList<FaceTab> faceTabs = new ArrayList<>();
                    FaceTab tab = initAssetsFace(context);
                    if (tab != null)
                        faceTabs.add(tab);

                    tab = initResourceFace(context);
                    if (tab != null)
                        faceTabs.add(tab);

                    for (FaceTab faceTab : faceTabs) {
                        faceTab.copyToMap(FACE_MAP);
                    }

                    FACE_TABS = Collections.unmodifiableList(faceTabs);
                }
            }
    }

    private static FaceTab initAssetsFace(Context context) {
        String faceAsset = "face-t.zip";
        String faceCacheDir = String.format("%s/face/tf", context.getFilesDir());
        File faceFolder = new File(faceCacheDir);

        if (!faceFolder.exists()) {
            if (faceFolder.mkdirs()) {
                try {
                    InputStream inputStream = context.getAssets().open(faceAsset);
                    File faceSource = new File(faceFolder, "source.zip");
                    StreamUtil.copy(inputStream, faceSource);
                    unZipFile(faceSource, faceFolder);
                    StreamUtil.delete(faceSource.getAbsolutePath()); // 清理文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File infoFile = new File(faceCacheDir, "info.json");
        Gson gson = new Gson();
        JsonReader reader;
        try {
            reader = gson.newJsonReader(new FileReader(infoFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        FaceTab tab = gson.fromJson(reader, FaceTab.class);
        // 相对路径到绝对路径
        for (Bean face : tab.faces) {
            face.preview = String.format("%s/%s", faceCacheDir, face.preview);
            face.source = String.format("%s/%s", faceCacheDir, face.source);
        }

        return tab;
    }

    /**
     * 将zipFile解压到desDir
     *
     * @param zipFile 待解压的源文件
     * @param desDir  解压后保存为此文件
     */
    private static void unZipFile(File zipFile, File desDir) throws IOException {
        final String folderPath = desDir.getAbsolutePath();

        ZipFile zf = new ZipFile(zipFile);

        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String name = entry.getName();
            if (name.startsWith("."))
                continue;

            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + name;

            str = new String(str.getBytes("8859_1"), "GB2312");

            File desFile = new File(str);
            StreamUtil.copy(in, desFile);
        }
    }

    // 从drawable资源中加载数据并映射到对应的KEY
    private static FaceTab initResourceFace(Context context) {
        final ArrayList<Bean> faces = new ArrayList<>();
        final Resources resources = context.getResources();

        String packageName = context.getApplicationInfo().packageName;
        for (int i = 1; i <= 142; i++) {
            String key = String.format(Locale.ENGLISH, "fb%03d", i);
            String resStr = String.format(Locale.ENGLISH, "face_base_%03d", i);
            int resId = resources.getIdentifier(resStr, "drawable", packageName);
            if (resId == 0)
                continue;

            faces.add(new Bean(key, resId));
        }

        if (faces.size() == 0)
            return null;

        return new FaceTab("NAME", faces.get(0).preview, faces);
    }

    public static List<FaceTab> all(@NonNull Context context) {
        init(context);
        return FACE_TABS;
    }

    public static void inputFace(@NonNull final Context context, final Editable editable,
                                 final Face.Bean bean, final int size) {
        Glide.with(context)
                .load(bean.source)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(size, size) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Spannable spannable = new SpannableString(String.format("[%s]", bean.key));
                        ImageSpan span = new ImageSpan(context, resource, ImageSpan.ALIGN_BASELINE);
                        spannable.setSpan(span, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editable.append(spannable);
                    }
                });
    }

    public static Bean get(Context context, String key) {
        init(context);
        if (FACE_MAP.containsKey(key)) {
            return FACE_MAP.get(key);
        }

        return null;
    }

    public static Spannable decode(@NonNull View target, final Spannable spannable, final int size) {
        if (spannable == null)
            return null;

        String str = spannable.toString();
        if (TextUtils.isEmpty(str))
            return null;

        final Context context = target.getContext();

        Pattern pattern = Pattern.compile("(\\[[^\\[\\]:\\s\\n]+\\])");
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            String key = matcher.group();
            if (TextUtils.isEmpty(key))
                continue;

            Bean bean = get(context, key.replace("[", "").replace("]", ""));
            if (bean == null)
                continue;

            final int start = matcher.start();
            final int end = matcher.end();

            ImageSpan span = new FaceSpan(context, target, bean.preview, size);

            spannable.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static class FaceSpan extends ImageSpan {

        private Drawable mDrawable;
        private View mView;
        private int mSize;

        /**
         * 构造
         *
         * @param context 上下文
         * @param view    目标View，用于加载完成时刷新使用
         * @param source  加载目标
         * @param size    图片的显示大小
         */
        public FaceSpan(Context context, View view, Object source, final int size) {
            super(context, R.drawable.default_face, ALIGN_BOTTOM);
            this.mView = view;
            this.mSize = size;

            Glide.with(context)
                    .load(source)
                    .fitCenter()
                    .into(new SimpleTarget<GlideDrawable>(size, size) {

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            mDrawable = resource.getCurrent();
                            int width = mDrawable.getIntrinsicWidth();
                            int height = mDrawable.getIntrinsicHeight();
                            mDrawable.setBounds(0, 0, width > 0 ? width : size,
                                    height > 0 ? height : size);

                            mView.invalidate();
                        }
                    });
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {

            Rect rect = mDrawable != null ? mDrawable.getBounds() : new Rect(0, 0, mSize, mSize);

            if (fm != null) {
                fm.ascent = -rect.bottom;
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
            return rect.right;
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if (mDrawable != null)
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    /**
     * 每一个表情盘
     */
    public static class FaceTab {
        public List<Bean> faces = new ArrayList<>();
        public String name;
        public Object preview;

        FaceTab(String name, Object preview, List<Bean> faces) {
            this.name = name;
            this.preview = preview;
            this.faces = faces;
        }

        void copyToMap(ArrayMap<String, Bean> faceMap) {
            for (Bean face : faces) {
                faceMap.put(face.key, face);
            }
        }
    }

    /**
     * 每一个表情
     */
    public static class Bean {

        Bean(String key, int preview) {
            this.key = key;
            this.source = preview;
            this.preview = preview;
        }

        public String key;
        public String desc;
        public Object source;
        public Object preview;
    }
}
