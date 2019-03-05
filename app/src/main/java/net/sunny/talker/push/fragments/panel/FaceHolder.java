package net.sunny.talker.push.fragments.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;

import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.push.R;

import butterknife.BindView;

/**
 * Created by sunny on 17-7-16.
 */

public class FaceHolder extends BaseRecyclerAdapter.BaseViewHolder<Face.Bean> {

    @BindView(R.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean != null && ((
                bean.preview instanceof Integer) // drawable资源
                || bean.preview instanceof String)) { // face zip 包资源路径

            Glide.with(mFace.getContext())
                    .load(bean.preview)
                    .asBitmap()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(mFace);
        }
    }
}
