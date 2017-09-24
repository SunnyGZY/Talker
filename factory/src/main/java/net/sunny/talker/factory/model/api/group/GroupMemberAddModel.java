package net.sunny.talker.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sunny on 17-7-10.
 */

public class GroupMemberAddModel {
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
