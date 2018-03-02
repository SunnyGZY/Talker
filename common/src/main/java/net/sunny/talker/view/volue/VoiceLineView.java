package net.sunny.talker.view.volue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.sunny.talker.common.R;

import static android.R.attr.mode;

/**
 * Created by carlos on 2016/1/29.
 * 自定义声音振动曲线view
 */
public class VoiceLineView extends View {

    private int middleLineColor = Color.BLACK;
    private int voiceLineColor = Color.BLACK;
    private float middleLineHeight = 4;
    private Paint paint;
    private Paint paintVoiceLine;
    /**
     * 灵敏度
     */
    private int sensibility = 4;

    private float maxVolume = 100;

    private float translateX = 0;
    private boolean isSet = false;

    /**
     * 音量
     */
    private float volume = 0;
    private int fineness = 1;
    private float targetVolume = 1;

    private long lastTime = 0;
    private int lineSpeed = 90;

    List<Path> paths = null;

    public VoiceLineView(Context context) {
        super(context);
    }

    public VoiceLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
    }

    public VoiceLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
    }

    /**
     * 初始化参数
     *
     * @param context 上下文
     * @param attrs   资源
     */
    private void initAtts(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.voiceView);
        voiceLineColor = typedArray.getColor(R.styleable.voiceView_voiceLine, Color.BLACK);
        maxVolume = typedArray.getFloat(R.styleable.voiceView_maxVolume, 100);
        sensibility = typedArray.getInt(R.styleable.voiceView_sensibility, 4);
        middleLineColor = typedArray.getColor(R.styleable.voiceView_middleLine, Color.BLACK);
        middleLineHeight = typedArray.getDimension(R.styleable.voiceView_middleLineHeight, 4);
        lineSpeed = typedArray.getInt(R.styleable.voiceView_lineSpeed, 90);
        fineness = typedArray.getInt(R.styleable.voiceView_fineness, 3);

        paths = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            paths.add(new Path());
        }
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMiddleLine(canvas);
        drawVoiceLine(canvas);

        postInvalidateDelayed(1/60);
    }

    /**
     * 绘制中间实线
     *
     * @param canvas 画布
     */
    private void drawMiddleLine(Canvas canvas) {
        if (paint == null) {
            paint = new Paint();
            paint.setColor(middleLineColor);
            paint.setAntiAlias(true);
        }
        canvas.save();
        canvas.drawRect(0, getHeight() / 2 - middleLineHeight / 2, getWidth(), getHeight() / 2 + middleLineHeight / 2, paint);
        canvas.restore();
    }

    /**
     * 绘制声波
     *
     * @param canvas 画布
     */
    private void drawVoiceLine(Canvas canvas) {
        lineChange();
        if (paintVoiceLine == null) {
            paintVoiceLine = new Paint();
            paintVoiceLine.setColor(voiceLineColor);
            paintVoiceLine.setAntiAlias(true);
            paintVoiceLine.setStyle(Paint.Style.STROKE);
            paintVoiceLine.setStrokeWidth(2);
        }
        canvas.save();
        int moveY = getHeight() / 2;
        for (int i = 0; i < paths.size(); i++) {
            paths.get(i).reset();
            paths.get(i).moveTo(getWidth(), getHeight() / 2);
        }

        for (float i = getWidth() - 1; i >= 0; i -= fineness) {

            // 振幅
            float amplitude = 4 * volume * i / getWidth() - 4 * volume * i * i / getWidth() / getWidth();
            for (int n = 1; n <= paths.size(); n++) {
                float sin = amplitude * (float) Math.sin((i - Math.pow(1.22, n)) * Math.PI / 180 - translateX);
                paths.get(n - 1).lineTo(i, (2 * n * sin / paths.size() - 15 * sin / paths.size() + moveY));
            }
        }

        for (int n = 0; n < paths.size(); n++) {
            if (n == paths.size() - 1) {
                paintVoiceLine.setAlpha(255);
            } else {
                paintVoiceLine.setAlpha(n * 130 / paths.size());
            }
            if (paintVoiceLine.getAlpha() > 0) {
                canvas.drawPath(paths.get(n), paintVoiceLine);
            }
        }
        canvas.restore();
    }

    private void lineChange() {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            translateX += 1.5;
        } else {
            if (System.currentTimeMillis() - lastTime > lineSpeed) {
                lastTime = System.currentTimeMillis();
                translateX += 1.5;
            } else {
                return;
            }
        }
        if (volume < targetVolume && isSet) {
            volume += getHeight() / 30;
        } else {
            isSet = false;
            if (volume <= 10) {
                volume = 10;
            } else {
                if (volume < getHeight() / 30) {
                    volume -= getHeight() / 60;
                } else {
                    volume -= getHeight() / 30;
                }
            }
        }
    }

    /**
     * 设置当前的声音大小
     *
     * @param volume 声音大小
     */
    public void setVolume(int volume) {
        if (volume > maxVolume * sensibility / 25) {
            isSet = true;
            this.targetVolume = getHeight() * volume / 2 / maxVolume;
        }
    }
}