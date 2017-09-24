package net.sunny.talker.push.fragments.main;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.sunny.talker.common.app.Fragment;
import net.sunny.talker.push.R;
import net.sunny.talker.push.fragments.track.FriendTrackFragment;
import net.sunny.talker.push.fragments.track.OnPagerChangeListener;
import net.sunny.talker.push.fragments.track.SchoolTrackFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sunny on 17-7-24.
 * 好友动态Fragment
 */

public class TrackFragment extends Fragment implements OnPagerChangeListener {

    @BindView(R.id.id_viewpager)
    public ViewPager mViewPager;

    private List<Fragment> mFragments = new ArrayList<>();

    public TrackFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_track;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        Fragment schoolTrackFragment = new SchoolTrackFragment(this);
        Fragment friendTrackFragment = new FriendTrackFragment(this);
        mFragments.add(schoolTrackFragment);
        mFragments.add(friendTrackFragment);

        // FragmentPagerAdapter 适配器
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);

        // ViewPager 的滑动事件监听
        mViewPager.setOnPageChangeListener(listener);
    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            int currentItem = mViewPager.getCurrentItem();
            // 这里可以根据当前的 currentItem 做不同的处理
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
    }

    @Override
    public void changeViewPager(Fragment fragment) {
        if (mFragments.contains(fragment)) {
            int index = mFragments.indexOf(fragment);
            mViewPager.setCurrentItem((mFragments.size() - index) * 2 / 3);
        }
    }
}
