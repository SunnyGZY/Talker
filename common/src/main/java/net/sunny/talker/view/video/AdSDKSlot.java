package net.sunny.talker.view.video;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.sunny.talker.utils.Utils;
import net.sunny.talker.view.video.constant.AdContextParameters;
import net.sunny.talker.view.video.constant.SDKConstant;

/**
 * 2018年1月9日16:52:47
 * function: 业务逻辑层
 */
public class AdSDKSlot implements CustomVideoView.ADVideoPlayerListener {

    private Context mContext;
    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    /**
     * Data
     */
    private VideoSDKListener mSlotListener;
    private boolean canPause = false; //是否可自动暂停标志位
    private boolean isFirst = false;
    private int lastArea = 0; //防止将要滑入滑出时播放器的状态改变
    private String url;

    public AdSDKSlot(String adInstance,
                     ViewGroup parentView, VideoSDKListener slotListener) {
        this.url = adInstance;
        this.mSlotListener = slotListener;
        this.mParentView = parentView;
        this.mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {

        mVideoView = new CustomVideoView(mContext, mParentView);
        if (mSlotListener != null) {
            mSlotListener.getVideoView(mVideoView);
        }

        if (!url.isEmpty()) {
            mVideoView.setDataSource(url);

            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    /**
     * 判断视频是否正在播放
     *
     * @return true 正在播放
     */
    private boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    /**
     * 判断视频是否暂停
     *
     * @return true 暂停
     */
    private boolean isPauseBtnClick() {
        if (mVideoView != null) {
            return mVideoView.isPauseBtnClicked();
        }
        return false;
    }

    /**
     * 判断视频是否播放完成
     *
     * @return true 播放完成
     */
    private boolean isComplete() {
        if (mVideoView != null) {
            return mVideoView.isComplete();
        }
        return false;
    }

    /**
     * 暂停视频
     */
    private void pauseVideo() {
        if (mVideoView != null) {
            mVideoView.seekAndPause(0);
        }
    }

    /**
     * resume the video
     */
    private void resumeVideo() {
        if (mVideoView != null) {
            mVideoView.resume();
            if (isPlaying() && isFirst) {
                isFirst = false;
            }
        }
    }

    /**
     * 判断视频当前是否播放
     */
    public void updateAdInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //小于0表示未出现在屏幕上，不做任何处理
        if (currentArea <= 0) {
            return;
        }
        //刚要滑入和滑出时，异常状态的处理
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }
        if (currentArea < SDKConstant.VIDEO_SCREEN_PERCENT) {
            //进入自动暂停状态
            if (canPause) {
                pauseVideo();
                canPause = false;
                //isFirst = true;
            }
            lastArea = 0;
            isFirst = true;
            mVideoView.setIsComplete(false); // 滑动出50%后标记为从头开始播
            mVideoView.setIsRealPause(false);
            return;
        }

        if (isPauseBtnClick() || isComplete()) {
            //进入手动暂停或者播放结束，播放结束和不满足自动播放条件都作为手动暂停
            pauseVideo();
            canPause = false;
            return;
        }

        //满足自动播放条件或者用户主动点击播放，开始播放
        if (Utils.canAutoPlay(mContext, AdContextParameters.getCurrentSetting())
                || isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            mVideoView.setIsRealPause(false);
        } else {
            pauseVideo();
            mVideoView.setIsRealPause(true); //不能自动播放则设置为手动暂停效果
        }
    }

    /**
     * 实现play层接口
     */
    @Override
    public void onClickFullScreenBtn() {

        if (mSlotListener != null) {
            mSlotListener.onPlayInFullScreen(true);
        }

        mParentView.removeView(mVideoView);
        VideoFullDialog dialog = new VideoFullDialog(mContext, mVideoView,
                mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
        dialog.setSlotListener(mSlotListener);
        dialog.show();
    }

    private void backToSmallMode(int position) {

        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        isFirst = false; // 防止多发送一次sus
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position);
        canPause = true; // 标为可自动暂停
    }

    private void bigPlayComplete() {
        if (mSlotListener != null) {
            mSlotListener.onVideoPlayComplete();
            mSlotListener.onPlayInFullScreen(false);
        }

        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
        canPause = false;
    }

    @Override
    public void onClickVideo() {
        if (mSlotListener != null) {
            mSlotListener.onClickVideo();
        }
    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {
        // TODO: 2018/1/9 点击播放按钮
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadSuccess();
        }
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadFailed();
        }
        //加载失败全部回到初始状态
        canPause = false;
    }

    @Override
    public void onAdVideoPlayComplete() {
        if (mSlotListener != null) {
            mSlotListener.onVideoPlayComplete();
        }

        mVideoView.setIsRealPause(true);
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
        /**
         * 将BufferUpdate事件发送到App层
         */
        if (mSlotListener != null) {
            mSlotListener.onBufferUpdate(time);
        }
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        url = null;
    }

    public static class VideoSDKListenerImpl implements VideoSDKListener {

        @Override
        public void onAdVideoLoadSuccess() {

        }

        @Override
        public void onAdVideoLoadFailed() {

        }

        @Override
        public void onVideoPausing() {

        }

        @Override
        public void onVideoPlaying() {

        }

        @Override
        public void onBufferUpdate(int time) {

        }

        @Override
        public void onVideoStop() {

        }

        @Override
        public void onVideoPlayComplete() {

        }

        @Override
        public void onClickVideo() {

        }

        @Override
        public void onPlayInFullScreen(boolean isPlayInFullScreen) {

        }

        @Override
        public void getVideoView(CustomVideoView customVideoView) {

        }
    }

    //传递消息到App层
    interface VideoSDKListener {

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onVideoStop();

        void onVideoPausing();

        void onVideoPlaying();

        void onBufferUpdate(int time);

        void onVideoPlayComplete();

        void onClickVideo();

        void onPlayInFullScreen(boolean isPlayInFullScreen);

        void getVideoView(CustomVideoView customVideoView);
    }
}
