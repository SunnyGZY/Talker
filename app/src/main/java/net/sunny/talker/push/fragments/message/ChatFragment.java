package net.sunny.talker.push.fragments.message;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;
import net.sunny.talker.common.app.Application;
import net.sunny.talker.common.app.PresenterFragment;
import net.sunny.talker.common.tools.AudioPlayHelper;
import net.sunny.talker.common.widget.PortraitView;
import net.sunny.talker.common.widget.adapter.TextWatcherAdapter;
import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.factory.model.db.Message;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.persistence.Account;
import net.sunny.talker.factory.presenter.message.ChatContract;
import net.sunny.talker.factory.utils.FileCache;
import net.sunny.talker.push.R;
import net.sunny.talker.push.activities.MessageActivity;
import net.sunny.talker.push.fragments.panel.PanelFragment;
import net.sunny.talker.utils.DateTimeUtil;
import net.sunny.talker.view.GalleryDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunny on 2017/6/15.
 * Email：670453367@qq.com
 * Description: 聊天的父类Fragment
 */

public abstract class ChatFragment<InitModel> extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener, ChatContract.View<InitModel>, PanelFragment.PanelCallback {

    protected String mReceiverId;
    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;

    // 控制地步面板与软件盘过渡的控件
    private AirPanel.Boss mPaneBoss;
    private PanelFragment mPanelFragment;

    private FileCache<AudioHolder> mAudioFileCache;
    AudioPlayHelper<AudioHolder> mAudioPlayer;

    private int position = 0;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    // 得到顶部布局资源Id
    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWidget(View root) {
        // 拿到占位布局，替换顶部布局
        ViewStub stub = (ViewStub) root.findViewById(R.id.view_stub_header);
        stub.setLayoutResource(getHeaderLayoutId());
        stub.inflate();

        super.initWidget(root);

        // 初始化面板操作
        mPaneBoss = (AirPanel.Boss) root.findViewById(R.id.lay_content);
        mPaneBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                // 请求隐藏软件盘
                Util.hideKeyboard(mContent);
            }
        });
        mPaneBoss.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                // 面板改变
                if (isOpen) {
                    if (position >= 10)
                        mRecyclerView.smoothScrollToPosition(position);
                    onBottomPanelOpened();
                }

            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {
                // 软件盘改变
                if (isOpen) {
                    if (position >= 10)
                        mRecyclerView.smoothScrollToPosition(position);
                    onBottomPanelOpened();
                }
            }
        });

        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);

        initToolbar();
        initAppbar();
        initEditContent();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);

        // 添加适配器监听器
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Message>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {
                if (message.getType() == Message.TYPE_AUDIO && holder instanceof ChatFragment.AudioHolder) {
                    mAudioFileCache.download((AudioHolder) holder, message.getContent()); // 权限已经全局申请
                } else if (message.getType() == Message.TYPE_PIC && holder instanceof ChatFragment.PicHolder) {
                    showGalleryDialog(message);
                }
            }

            @Override
            public void getItemCount(int count) {
                if (position != count - 1) {
                    position = count - 1;

                    mRecyclerView.scrollToPosition(position);
                }
            }
        });
    }

    private void showGalleryDialog(Message message) {
        ArrayList<String> imageUrls = new ArrayList<>();

        for (Message msg : mAdapter.getItems()) {
            if (msg.getType() == Message.TYPE_PIC) {
                imageUrls.add(msg.getContent());
            }
        }

        // 找当前点击的图片在所有图片中的位置
        int position = 0;
        for (int i = 0; i < imageUrls.size(); i++) {
            if (imageUrls.get(i).equalsIgnoreCase(message.getContent()))
                position = i;
        }

        final GalleryDialog galleryDialog = new GalleryDialog(getContext(), imageUrls, position);
        galleryDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.destroy();
    }

    private void onBottomPanelOpened() {
        // 当底部面板或者软件盘打开时触发
        if (mAppBarLayout != null)
            mAppBarLayout.setExpanded(false, true);
    }

    @Override
    public boolean onBackPressed() {
        if (mPaneBoss.isOpen()) {
            mPaneBoss.closePanel();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAudioPlayer = new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {
            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_audio_play_error);
            }
        });

        mAudioFileCache = new FileCache<>("audio/cache", "mp3", new FileCache.CacheListener<AudioHolder>() {

            @Override
            public void onDownloadSucceed(final AudioHolder holder, final File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mAudioPlayer.trigger(holder, file.getAbsolutePath());
                    }
                });
            }

            @Override
            public void onDownloadFailed(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_download_error);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    // 给Appbar设置一个监听，得到关闭与打开的时候的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        mPaneBoss.openPanel();
        mPanelFragment.showFace();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        mPaneBoss.openPanel();
        mPanelFragment.showRecord();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        mPaneBoss.openPanel();
        mPanelFragment.showGallery();
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // RecyclerView是一直显示的，所以不用做任何处理
    }

    @Override
    public EditText getInputEditText() {
        return mContent;
    }

    // 图片选择回调
    @Override
    public void onSendGallery(String[] paths) {
        mPresenter.pushImages(paths);
    }

    //  语音录制回调
    @Override
    public void onRecordDone(File file, long time) {
        mPresenter.pushAudio(file.getAbsolutePath(), time);
    }

    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemView(int position, Message message) {
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                case Message.TYPE_STR: // 文本
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                case Message.TYPE_AUDIO: // 语音
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                case Message.TYPE_PIC: // 图片
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                case Message.TYPE_FOOTER:
                    return R.layout.cell_chat_footer;
                default: // TODO  文件
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);
                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);
                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);
                case R.layout.cell_chat_footer:
                    return new FooterHolder(root);
                default: // TODO 文件
                    return new TextHolder(root);
            }
        }
    }

    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            // 懒加载
            sender.load();
            mPortrait.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) { // 正常状态
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) { // 正在发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) { // 发送失败,允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                    mLoading.stop();
                }
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            if (mLoading != null && mPresenter.rePush(mData)) {
                updateData(mData);
            }
        }
    }

    /**
     * 文本
     */
    class TextHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            Spannable spannable = new SpannableString(message.getContent());
            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));
            mContent.setText(spannable);
        }
    }

    /**
     * 语音
     */
    class AudioHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;

        AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            String attach = TextUtils.isEmpty(message.getAttach()) ? "0" : message.getAttach();
            mContent.setText(formatTime(attach));
        }

        void onPlayStart() {
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        void onPlayStop() {
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private String formatTime(String attach) {
            float time;
            try {
                time = Float.parseFloat(attach) / 1000f;
            } catch (Exception e) {
                time = 0;
            }

            String shortTime = String.valueOf(Math.round(time * 10) / 10f);
            return String.format("%s", shortTime);
        }
    }

    /**
     * 图片
     */
    class PicHolder extends BaseHolder {

        @BindView(R.id.im_image)
        ImageView mContent;

        PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            String content = message.getContent();

            Glide.with(ChatFragment.this)
                    .load(content)
                    .fitCenter()
                    .into(mContent);
        }
    }

    class FooterHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.footer)
        TextView mFooter;

        FooterHolder(View root) {
            super(root);
        }

        @Override
        protected void onBind(Message message) {
            String time = DateTimeUtil.getWeekData(message.getCreateAt());
            String str = getString(R.string.label_last_chat_time) + time;
            mFooter.setText(str);
        }
    }
}
