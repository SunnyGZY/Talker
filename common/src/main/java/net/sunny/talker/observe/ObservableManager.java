package net.sunny.talker.observe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ObservableManager<Param, Result>
        implements ObservableInterface<Function, Param, Result> {

    private HashMap<String, Set<Function>> _mapping;
    private final Object _lockObj = new Object();
    private static ObservableManager _instance;

    private ObservableManager() {
        this._mapping = new HashMap<>();
    }

    public static ObservableManager newInstance() {
        if (_instance == null) _instance = new ObservableManager();
        return _instance;
    }

    @Override
    public void registerObserver(String name, Function observer) {
        synchronized (_lockObj) {
            Set<Function> observers;
            if (!_mapping.containsKey(name)) {
                observers = new HashSet<>();
                _mapping.put(name, observers);
            } else {
                observers = _mapping.get(name);
            }
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(String name) {
        synchronized (_lockObj) {
            _mapping.remove(name);
        }
    }

    @Override
    public void removeObserver(Function observer) {
        synchronized (_lockObj) {
            for (String key : _mapping.keySet()) {
                Set<Function> observers = _mapping.get(key);
                observers.remove(observer);
            }
        }
    }

    @Override
    public void removeObserver(String name, Function observer) {
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                Set<Function> observers = _mapping.get(name);
                observers.remove(observer);
            }
        }
    }

    @Override
    public Set<Function> getObserver(String name) {
        Set<Function> observers = null;
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                observers = _mapping.get(name);
            }
        }
        return observers;
    }

    @Override
    public void clear() {
        synchronized (_lockObj) {
            _mapping.clear();
        }
    }

    @Override
    public final Result notify(String name, Param param) {
        synchronized (_lockObj) {
            if (_mapping.containsKey(name)) {
                Set<Function> observers = _mapping.get(name);
                for (Function o : observers) {
                    o.function(param);
                }
            }
            return null;
        }
    }
}