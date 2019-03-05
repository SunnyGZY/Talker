package net.sunny.talker.push.fragments.panel;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.qiujuer.genius.ui.Ui;
import net.sunny.talker.common.app.Application;
import net.sunny.talker.common.app.Fragment;
import net.sunny.talker.common.tools.AudioRecordHelper;
import net.sunny.talker.common.tools.UiTool;
import net.sunny.talker.common.widget.AudioRecordView;
import net.sunny.talker.common.widget.GalleryView;
import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.push.R;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanelFragment extends Fragment {

    private View mFacePanel, mGalleryPanel, mRecordPanel;
    private PanelCallback mCallback;

    public PanelFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_panel;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        initFace(root);
        initRecord(root);
        initGallery(root);
    }

    public void setup(PanelCallback callback) {
        mCallback = callback;
    }

    /**
     * 初始化表情面板
     *
     * @param root 根布局
     */
    private void initFace(final View root) {

        final View facePanel = mFacePanel = root.findViewById(R.id.lay_panel_face);
        View backspace = facePanel.findViewById(R.id.im_backspace);
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PanelCallback callback = mCallback;
                if (callback == null)
                    return;

                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                callback.getInputEditText().dispatchKeyEvent(event);
            }
        });

        TabLayout tabLayout = (TabLayout) facePanel.findViewById(R.id.tab);
        ViewPager viewPager = (ViewPager) facePanel.findViewById(R.id.pager);
        tabLayout.setupWithViewPager(viewPager);

        final int minFaceSize = (int) Ui.dipToPx(getResources(), 48);
        final int totalScreen = UiTool.getScreenWidth(getActivity());
        final int spanCount = totalScreen / minFaceSize;

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Face.all(getContext()).size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.lay_face_content, container, false);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

                // 设置Adapter
                List<Face.Bean> faces = Face.all(getContext()).get(position).faces;
                FaceAdapter adapter = new FaceAdapter(faces, new BaseRecyclerAdapter.AdapterListenerImpl<Face.Bean>() {
                    @Override
                    public void onItemClick(BaseRecyclerAdapter.BaseViewHolder holder, Face.Bean bean) {
                        if (mCallback == null)
                            return;
                        EditText editText = mCallback.getInputEditText();
                        Face.inputFace(getContext(), editText.getText(), bean,
                                (int) (editText.getTextSize() + Ui.dipToPx(getResources(), 2)));

                    }
                });
                recyclerView.setAdapter(adapter);

                container.addView(recyclerView);
                return recyclerView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                super.destroyItem(container, position, object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return Face.all(getContext()).get(position).name;
            }
        });
    }

    /**
     * 初始化录音面板
     *
     * @param root 根布局
     */
    private void initRecord(View root) {
        final View recordPanel = mRecordPanel = root.findViewById(R.id.lay_panel_record);
        final AudioRecordView audioRecordView = (AudioRecordView) recordPanel.findViewById(R.id.view_audio_record);
        File tmpFile = Application.getAudioTmpFile(true);
        final AudioRecordHelper helper = new AudioRecordHelper(tmpFile, new AudioRecordHelper.RecordCallback() {
            @Override
            public void onRecordStart() {

            }

            @Override
            public void onProgress(long time) {

            }

            @Override
            public void onRecordDone(File file, long time) {
                if (time < 1000) {
                    return;
                }

                File audioFile = Application.getAudioTmpFile(false);
                if (file.renameTo(audioFile)) {
                    PanelCallback panelCallback = mCallback;
                    if (panelCallback != null) {
                        panelCallback.onRecordDone(audioFile, time);
                    }
                }
            }

            @Override
            public void onVolume(double volume) {
                audioRecordView.setVol((int) volume);
            }
        });

        audioRecordView.setup(new AudioRecordView.Callback() {
            @Override
            public void requestStartRecord() {
                // 请求开始
                helper.recordAsync();
            }

            @Override
            public void requestStopRecord(int type) {
                // 请求结束
                switch (type) {
                    case AudioRecordView.END_TYPE_CANCEL:
                    case AudioRecordView.END_TYPE_DELETE:
                        helper.stop(true);
                        break;
                    case AudioRecordView.END_TYPE_NONE:
                    case AudioRecordView.END_TYPE_PLAY:
                        helper.stop(false);
                        break;
                }
            }
        });
    }

    /**
     * 初始化图片
     *
     * @param root 根布局
     */
    private void initGallery(View root) {
        final View galleryPanel = mGalleryPanel = root.findViewById(R.id.lay_gallery_panel);
        final GalleryView galleryView = (GalleryView) galleryPanel.findViewById(R.id.view_gallery);
        final TextView selectedSize = (TextView) galleryPanel.findViewById(R.id.txt_gallery_select_count);

        galleryView.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {
                String str = String.format(getText(R.string.label_gallery_selected_size).toString(), count);
                selectedSize.setText(str);
            }
        });

        galleryPanel.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGallerySendClick(galleryView, galleryView.getSelectedPath());
            }
        });
    }

    private void onGallerySendClick(GalleryView galleryView, String[] paths) {
        galleryView.clear();
        PanelCallback callback = mCallback;
        if (callback == null)
            return;

        callback.onSendGallery(paths);
    }

    public void showFace() {
        mFacePanel.setVisibility(View.VISIBLE);
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.GONE);
    }

    public void showRecord() {
        mFacePanel.setVisibility(View.GONE);
        mRecordPanel.setVisibility(View.VISIBLE);
        mGalleryPanel.setVisibility(View.GONE);
    }

    public void showGallery() {
        mFacePanel.setVisibility(View.GONE);
        mRecordPanel.setVisibility(View.GONE);
        mGalleryPanel.setVisibility(View.VISIBLE);
    }

    public interface PanelCallback {

        EditText getInputEditText();

        // 返回需要发送的图片
        void onSendGallery(String[] paths);

        // 返回录音的文件和时长
        void onRecordDone(File file, long time);
    }
}
