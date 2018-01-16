package net.sunny.talker.view.video;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.sunny.talker.common.R;


/**
 * @author: qndroid
 * @function: 全屏显示视频
 * @date: 16/6/7
 */
public class VideoFullDialog extends Dialog
        implements CustomVideoView.ADVideoPlayerListener {

    private CustomVideoView mVideoView;

    private ViewGroup mParentView;

    private int mPosition;
    private FullToSmallListener mListener;
    private boolean isFirst = true;

    private AdSDKSlot.VideoSDKListener mSlotListener;

    public VideoFullDialog(Context context, CustomVideoView mediaView, int position) {
        super(context, R.style.dialog_full_screen);
        this.mPosition = position;
        this.mVideoView = mediaView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }

    public void setListener(FullToSmallListener listener) {
        this.mListener = listener;
    }

    public void setSlotListener(AdSDKSlot.VideoSDKListener slotListener) {
        this.mSlotListener = slotListener;
    }

    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        ImageView mBackButton = (ImageView) findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        RelativeLayout mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mVideoView.setListener(this);
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mVideoView.isShowFullBtn(false); //防止第一次，有些手机仍显示全屏按钮
        if (!hasFocus) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            if (isFirst) { //为了适配某些手机不执行seekandresume中的播放方法
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        isFirst = false;
    }

    @Override
    public void dismiss() {
        this.mParentView.removeView(mVideoView);
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        onClickBackBtn();
        super.onBackPressed();
    }

    @Override
    public void onClickFullScreenBtn() {
        onClickVideo();
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {
        this.dismiss();
        if (mSlotListener != null) {
            mSlotListener.onPlayInFullScreen(false);
        }

        if (mListener != null) {
            mListener.getCurrentPlayPosition(this.mVideoView.getCurrentPosition());
        }
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadFailed();
        }
    }

    @Override
    public void onAdVideoPlayComplete() {
        dismiss();
        if (mListener != null) {
            mListener.playComplete();
        }
    }

    @Override
    public void onVideoPlaying() {
        if (mSlotListener != null) {
            mSlotListener.onVideoPlaying();
        }
    }

    @Override
    public void onVideoPausing() {
        if (mSlotListener != null) {
            mSlotListener.onVideoPausing();
        }
    }

    @Override
    public void onVideoStop() {
        if (mSlotListener != null) {
            mSlotListener.onVideoStop();
        }
    }

    @Override
    public void onBufferUpdate(int time) {
        if (mSlotListener != null) {
            mSlotListener.onBufferUpdate(time);
        }
    }

    @Override
    public void onClickPlay() {
        if (mSlotListener != null) {
            mSlotListener.onClickVideo();
        }
    }

    public interface FullToSmallListener {
        void getCurrentPlayPosition(int position);

        void playComplete();//全屏播放结束时回调
    }
}
