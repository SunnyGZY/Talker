package net.sunny.talker.common.app;


import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.sunny.talker.common.R;

/**
 * Created by Sunny on 2017/6/1.
 * Email：670453367@qq.com
 * Description: TOOD
 */

public abstract class ToolbarActivity extends Activity {

    protected Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void initToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        initTitleNeedBack();
    }

    protected void initTitleNeedBack() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
