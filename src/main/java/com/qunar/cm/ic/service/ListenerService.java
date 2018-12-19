package com.qunar.cm.ic.service;

import com.qunar.cm.ic.dto.ListenerFetchResult;

import java.util.List;

/**
 * Created by yu.qi on 2018/08/29.
 */
public interface ListenerService {
    ListenerFetchResult consumeEvents(String token, List<String> types, int maxResults, boolean longPoll, String ip);

    void acknowledge(String token, String code, String ip);

}
