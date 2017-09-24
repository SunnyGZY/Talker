package net.sunny.talker.factory.presenter.group;

import net.sunny.talker.factory.model.Author;
import net.sunny.talker.factory.presenter.BaseContract;

/**
 * Created by sunny on 17-7-8.
 * 群创建
 */

public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter {
        void create(String name, String desc, String picture);

        // 更改一个Model的选中状态
        void changeSelect(ViewModel model, boolean isSelected);
    }

    interface View extends BaseContract.RecyclerView<Presenter, ViewModel> {
        void onCreateSuccess();
    }

    class ViewModel {
        public Author author;
        public boolean isSelect;
    }
}
