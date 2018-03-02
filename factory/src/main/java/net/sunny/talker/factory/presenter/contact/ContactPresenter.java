package net.sunny.talker.factory.presenter.contact;

import android.support.v7.util.DiffUtil;

import net.sunny.talker.common.widget.recycler.RecyclerAdapter;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.data.user.ContactDataSource;
import net.sunny.talker.factory.data.user.ContactRepository;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.BaseSourcePresenter;
import net.sunny.talker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * Created by Sunny on 2017/6/6.
 * Email：670453367@qq.com
 * Description: ContactPresenter
 */

// TODO: 17-7-26 here
public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View>
        implements ContactContract.Presenter {

    public ContactPresenter(ContactContract.View view) {
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();

        // 加载网络数据
        UserHelper.refreshContacts();
    }

    /**
     * 无论数据怎么变更，都会通知到这里来
     *
     * @param users
     */
    @Override
    public void onDataLoaded(List<User> users) {
        final ContactContract.View view = getView();
        if (view == null)
            return;

        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();

        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result, users);
    }
}
