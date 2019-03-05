package net.sunny.talker.push.fragments.panel;

import android.view.View;

import net.sunny.talker.common.widget.recycler.BaseRecyclerAdapter;
import net.sunny.talker.face.Face;
import net.sunny.talker.push.R;

import java.util.List;

/**
 * Created by sunny on 17-7-16.
 */

public class FaceAdapter extends BaseRecyclerAdapter<Face.Bean> {


    public FaceAdapter(List<Face.Bean> beans, AdapterListener<Face.Bean> listener) {
        super(beans, listener);
    }

    @Override
    protected int getItemView(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected BaseViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
