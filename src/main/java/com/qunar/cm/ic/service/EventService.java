package com.qunar.cm.ic.service;

import com.qunar.cm.ic.model.Event;

import java.util.List;

/**
 * Created by dandan.sha on 2018/08/24.
 */
public interface EventService {
    Event queryById(Long id);

    Event checkAndSaveEvent(Event event);

    List<Event> queryByTimeAndType(String type, String from, String to);
}
