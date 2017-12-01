package net.sunny.talker.factory.presenter.main;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.request.RequestRepository;
import net.sunny.talker.factory.model.db.User;
import net.sunny.talker.factory.presenter.SimplePresenter;

import java.util.List;

/**
 * Created by 67045 on 2017/10/26.
 * MainPresenter
 */
public class MainPresenter
        extends SimplePresenter<MainContract.View>
        implements MainContract.Presenter, DataSource.SucceedCallback<List<User>> {

    private RequestRepository requestRepository;

    public MainPresenter(MainContract.View view) {
        super(view);
        requestRepository = new RequestRepository();
    }

    @Override
    public void onDataLoaded(final List<User> users) {
        final MainContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showRequestMsgCount(users.size());
                }
            });
        }
    }

    @Override
    public void start() {
        super.start();
        if (requestRepository != null) {
            requestRepository.load(this);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        requestRepository.dispose();
        requestRepository = null;
    }
}
