package net.sunny.talker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.sunny.talker.common.app.ToolbarActivity;
import net.sunny.talker.push.R;
import net.sunny.talker.view.ProgressWebView;

import butterknife.BindView;

/**
 * 看一看
 */
public class LookLookActivity extends ToolbarActivity {

    @BindView(R.id.wv_content)
    ProgressWebView mWebView;

    public static void show(Context context) {
        Intent intent = new Intent(context, LookLookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_look_look;
    }

    @Override
    protected void initData() {
        super.initData();

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mWebView.loadUrl("https://xw.qq.com/");
    }
}
