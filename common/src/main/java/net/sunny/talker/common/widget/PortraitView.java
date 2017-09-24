package net.sunny.talker.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;

import net.sunny.talker.common.R;
import net.sunny.talker.factory.model.Author;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sunny on 2017/5/16.
 * Email：670453367@qq.com
 * Description: 用户头像封装类
 */

public class PortraitView extends CircleImageView {

    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, Author author) {
        if (author == null)
            return;
        setup(manager, author.getPortrait());
    }

    public void setup(RequestManager manager, String url) {
        setup(manager, R.drawable.default_portrait, url);
    }

    public void setup(RequestManager manager, int resourceId, String url) {
        if (url == null) {
            url = "";
        }
        manager.load(url)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate() // CircleImageView 控件中不能使用渐变动画,会导致显示延迟
                .into(this);
    }
}
