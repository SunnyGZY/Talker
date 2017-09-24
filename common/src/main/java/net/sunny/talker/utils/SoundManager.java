package net.sunny.talker.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import net.sunny.talker.common.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 软件提示音管理类
 */
public class SoundManager {

    private static final SoundManager instance;
    private SoundPool mSoundPool;
    private Map<String, Integer> resources;
    private boolean isLoadComplete;

    private SoundManager() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        resources = new HashMap<>();
        isLoadComplete = false;
    }

    static {
        instance = new SoundManager();
    }

    public static void loadResource(Context context) {

        final String key = String.valueOf(R.raw.tone_cue_normal); // 将resource的id当做Map的key

        int id = instance.mSoundPool.load(context, R.raw.tone_cue_normal, 1);
        instance.resources.put(key, id);

        instance.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == instance.resources.size())
                    instance.isLoadComplete = true;
            }
        });
    }

    public static void play(int resource) {
        String key = String.valueOf(resource);
        if (instance.isLoadComplete && instance.resources.containsKey(key))
            instance.mSoundPool.play(instance.resources.get(key), 1, 1, 0, 0, 1);
    }

    public static void release() {
        instance.mSoundPool.release();
    }
}