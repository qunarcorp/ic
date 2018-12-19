package com.qunar.cm.ic.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.dao.EventRepository;
import com.qunar.cm.ic.dao.ListenerRepository;
import com.qunar.cm.ic.dao.page.FirstEventPage;
import com.qunar.cm.ic.dto.IpMatcher;
import com.qunar.cm.ic.dto.ListenerFetchResult;
import com.qunar.cm.ic.model.Event;
import com.qunar.cm.ic.model.Listener;
import com.qunar.cm.ic.service.EventConsumerService;
import com.qunar.cm.ic.service.ListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

/**
 * Created by yu.qi on 2018/08/29.
 */
@Service
public class ListenerServiceImpl implements ListenerService {
    private static final Logger logger = LoggerFactory.getLogger(ListenerServiceImpl.class);

    @Resource
    private EventRepository eventRepository;
    @Resource
    private ListenerRepository listenerRepository;
    @Resource
    private EventConsumerService eventConsumerService;

    private ConcurrentMap<String, IpMatcher> ipMatcherMap = Maps.newConcurrentMap();


    @Override
    public ListenerFetchResult consumeEvents(String token, List<String> types, int maxResults, boolean longPoll, String ip) {

        Optional<Listener> optionalListener = listenerRepository.findByToken(token);
        Listener listener = optionalListener.orElseGet(() -> createTemporaryListener(token));
        if (!createIpMatcher(listener).match(ip)) {
            throw new ICException(ExceptionEnum.IP_LIMITED, ip + "没有访问权限");
        }
        ListenerFetchResult result = new ListenerFetchResult(listener.getCode(), maxResults);
        result.setList(Collections.emptyList());

        if (longPoll) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, 10);
            Date deadLine = calendar.getTime();

            while (result.isEmpty()) {
                try {
                    eventConsumerService.waitForNextEvent(token, deadLine);
                } catch (TimeoutException e) {
                    break;
                }
                fillResult(listener.getLastEventId(), types, maxResults, result);
            }
        } else {
            fillResult(listener.getLastEventId(), types, maxResults, result);
        }

        if (!result.isEmpty()) {
            //重新生成code，如果返回的结果为空，则code不变
            listener.setCode(UUID.randomUUID().toString());
            long recordEventId = result.getList().get(result.getList().size() - 1).getId();
            listener.setRecordEventId(recordEventId);
            listener.setReadTime(new Date());
            listenerRepository.save(listener);
        }
        result.setCode(listener.getCode());
        return result;
    }


    private void fillResult(long lastEventId, List<String> types, int maxResults,
                            ListenerFetchResult result) {
        List<Event> events;
        //多获取一个，用于判断hasMore
        int actualQueryCount = maxResults + 1;
        if (types.isEmpty()) {
            events = eventRepository.consumeEvent(
                    lastEventId, FirstEventPage.of(actualQueryCount));
        } else {
            events = eventRepository.consumeEventByTypes(
                    lastEventId, types, FirstEventPage.of(actualQueryCount));
        }

        boolean hasMore = false;
        if (events.size() == actualQueryCount) {
            events.remove(actualQueryCount - 1);
            hasMore = true;
        }
        result.setHasMore(hasMore);
        result.setList(events);
    }

    /**
     * 不支持并发，即多个用户同时用一个不存在的token时，有一个会报错
     */
    Listener createTemporaryListener(String token) {
        Listener listener = new Listener();
        listener.setToken(token);
        listener.setDetail(token);
        listener.setCode(UUID.randomUUID().toString());
        String matchAllIps = ".*";
        listener.setIps(Lists.newArrayList(matchAllIps));
        Optional<Event> optionalEvent = eventRepository.findFirstByOrderByIdDesc();
        if (optionalEvent.isPresent()) {
            listener.setLastEventId(optionalEvent.get().getId());
        } else {
            listener.setLastEventId(0L);
        }
        listener.setTemporary(true);
        //insert方法的返回值是一个包含id的listener，可以用于后续的save操作
        logger.info("Listener[token={}]不存在，本次自动创建，lastEventId为{}", token, listener.getLastEventId());
        return listenerRepository.insert(listener);
    }


    @Override
    public void acknowledge(String token, String code, String ip) {
        Optional<Listener> optionalListener = listenerRepository.findByToken(token);
        Listener listener = optionalListener.orElseThrow(() ->
                new ICException(ExceptionEnum.PARAMS_INVALID, "token的值无效"));
        if (!createIpMatcher(listener).match(ip)) {
            throw new ICException(ExceptionEnum.IP_LIMITED, ip + "没有访问权限");
        }
        if (!Objects.equals(listener.getCode(), code)) {
            throw new ICException(ExceptionEnum.PARAMS_INVALID, "code的值不正确");
        }
        if (listener.getRecordEventId() != null && !Objects.equals(listener.getLastEventId(), listener.getRecordEventId())) {
            listener.setLastEventId(listener.getRecordEventId());
            listener.setReplyTime(new Date());
            listenerRepository.save(listener);
        }
    }

    /**
     * 创建IpMatcher对象，用于校验ip权限，这个接口还会缓存结果
     */
    private IpMatcher createIpMatcher(Listener listener) {
        return ipMatcherMap.compute(listener.getToken(), (key, value) -> {
            if (value != null && Objects.equals(value.getIps(), listener.getIps())) {
                return value;
            }
            logger.info("Listener[token={}]创建或更新IpMatcher对象", listener.getToken());
            return new IpMatcher(listener.getIps());
        });
    }

}