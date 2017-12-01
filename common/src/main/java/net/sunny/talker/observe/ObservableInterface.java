package net.sunny.talker.observe;

import java.util.Set;

public interface ObservableInterface<T, P, R> {

    /**
     * 根据名称注册观察者
     */
    void registerObserver(String name, T observer);

    /**
     * 根据名称反注册观察者
     */
    void removeObserver(String name);

    /**
     * 根据观察者反注册
     */
    void removeObserver(T observer);

    /**
     * 根据名称和观察者反注册
     */
    void removeObserver(String name, T observer);

    /**
     * 根据名称获取观察者
     */
    Set<T> getObserver(String name);

    /**
     * 清除观察者
     */
    void clear();

    /**
     * 通知观察者
     *
     * @param name 名称
     * @param p    参数
     * @return 返回值
     */
    R notify(String name, P... p);
}