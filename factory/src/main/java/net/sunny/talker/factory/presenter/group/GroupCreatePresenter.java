package net.sunny.talker.factory.presenter.group;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.sunny.talker.factory.Factory;
import net.sunny.talker.factory.R;
import net.sunny.talker.factory.data.DataSource;
import net.sunny.talker.factory.data.helper.GroupHelper;
import net.sunny.talker.factory.data.helper.UserHelper;
import net.sunny.talker.factory.model.api.group.GroupCreateModel;
import net.sunny.talker.factory.model.card.GroupCard;
import net.sunny.talker.factory.model.db.view.UserSampleModel;
import net.sunny.talker.factory.net.UploadHelper;
import net.sunny.talker.factory.presenter.base.BaseRecyclerPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunny on 17-7-8.
 */

public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewModel, GroupCreateContract.View>
        implements GroupCreateContract.Presenter, DataSource.Callback<GroupCard> {

    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    private Set<String> users = new HashSet<>();

    @Override
    public void start() {
        super.start();

        Factory.runOnAsync(loader);
    }

    @Override
    public void create(final String name, final String desc, final String picture) {
        GroupCreateContract.View view = getView();
        view.showLoading();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) ||
                TextUtils.isEmpty(picture) || users.size() == 0) {
            view.showError(R.string.label_group_create_invalid);
            return;
        }

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = upLoadPicture(picture);
                if (TextUtils.isEmpty(url))
                    return;

                GroupCreateModel model = new GroupCreateModel(name, desc, url, users);
                GroupHelper.create(model, GroupCreatePresenter.this);
            }
        });
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewModel model, boolean isSelected) {
        if (isSelected)
            users.add(model.author.getId());
        else
            users.remove(model.author.getId());
    }

    /**
     * 同步上传头像
     *
     * @param path
     * @return
     */
    private String upLoadPicture(String path) {
        String url = UploadHelper.uploadPortrait(path);

        if (TextUtils.isEmpty(url)) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = (GroupCreateContract.View) getView();
                    if (view != null)
                        view.showError(R.string.data_upload_error);
                }
            });
        }
        return url;
    }

    private Runnable loader = new Runnable() {
        @Override
        public void run() {
            List<UserSampleModel> sampleModels = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewModel> models = new ArrayList<>();
            for (UserSampleModel sampleModel : sampleModels) {
                GroupCreateContract.ViewModel viewModel = new GroupCreateContract.ViewModel();
                viewModel.author = sampleModel;
                models.add(viewModel);
            }
            refreshData(models);
        }
    };

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = (GroupCreateContract.View) getView();
                if (view != null) {
                    view.onCreateSuccess();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = (GroupCreateContract.View) getView();
                if (view != null) {
                    view.showError(strRes);
                }
            }
        });
    }
}
