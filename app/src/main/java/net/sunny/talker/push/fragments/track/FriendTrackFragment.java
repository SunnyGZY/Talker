package net.sunny.talker.push.fragments.track;


import android.widget.LinearLayout;

import net.sunny.talker.common.app.Fragment;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.main.TrackFragment;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendTrackFragment extends Fragment {


    OnPagerChangeListener listener;

    @BindView(R.id.ll_school)
    LinearLayout showSchool;

    public FriendTrackFragment(TrackFragment trackFragment) {
        listener = trackFragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_track_friend;
    }

    @OnClick(R.id.ll_school)
    public void showSchool() {
        if (listener != null)
            listener.changeViewPager(this);
    }
}
