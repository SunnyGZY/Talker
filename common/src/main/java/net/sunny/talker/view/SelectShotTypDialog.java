package net.sunny.talker.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;

import net.sunny.talker.common.R;

public class SelectShotTypDialog extends Dialog {

    private OnSelectTypeListener listener;

    /**
     * 指定显示第几张图片
     *
     * @param context 上下文
     */
    public SelectShotTypDialog(Context context, OnSelectTypeListener listener) {
        this(context, R.style.CustomDialog);
        this.listener = listener;
    }

    public SelectShotTypDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_shot_type);
        //按空白处不能取消动画  
        setCanceledOnTouchOutside(false);

        initClick();
    }

    private void initClick() {

        findViewById(R.id.tv_shot_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.shotPhoto();
                dismiss();
            }
        });

        findViewById(R.id.tv_shot_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.shotVideo();
                dismiss();
            }
        });

        findViewById(R.id.tv_from_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.selectFromAlbum();
                dismiss();
            }
        });
    }

    public interface OnSelectTypeListener {

        void shotPhoto();

        void shotVideo();

        void selectFromAlbum();
    }
}