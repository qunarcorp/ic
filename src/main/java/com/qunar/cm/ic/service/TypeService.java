package com.qunar.cm.ic.service;

import com.qunar.cm.ic.model.Event;
import com.qunar.cm.ic.model.Type;

import java.util.List;

/**
 * Created by dandan.sha on 2018/08/29.
 */
public interface TypeService {
    void checkEvent(Event event);

    List<Type> allTypes();

    Type getType(String name);
}

