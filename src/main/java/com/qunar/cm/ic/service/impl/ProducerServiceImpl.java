package com.qunar.cm.ic.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.dao.ProducerRepository;
import com.qunar.cm.ic.dto.IpMatcher;
import com.qunar.cm.ic.model.Producer;
import com.qunar.cm.ic.service.ProducerService;
import com.qunar.cm.ic.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dandan.sha on 2018/09/11.
 */
@Service
public class ProducerServiceImpl implements ProducerService {
    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);
    @Resource
    private ProducerRepository producerRepository;
    @Resource
    private PropertyService propertyService;

    private static final String PRODUCER_CACHE_KEY = "cache.producer";
    private volatile ConcurrentMap<String, IpMatcher> ipMatcherMap;

    @Override
    public void checkIp(String name, String ip) {
        Preconditions.checkNotNull(ipMatcherMap, "ProducerCache尚未初始化完成");
        IpMatcher ipMatcher = ipMatcherMap.get(name);
        if (!ipMatcher.match(ip)) {
            throw new ICException(ExceptionEnum.IP_LIMITED, ip + "没有访问权限");
        }
    }

    private synchronized void refreshIpMatcherMap() {
        List<Producer> producers = producerRepository.findAll();
        ConcurrentMap<String, IpMatcher> newIpMatcherMap = Maps.newConcurrentMap();
        producers.forEach(producer ->
                newIpMatcherMap.put(producer.getName(), new IpMatcher(producer.getIps())));
        ipMatcherMap = newIpMatcherMap;
        logger.info("更新Producer的ip列表成功，更新的Producer数量为{}", producers.size());
    }

    @Scheduled(fixedDelay = 5000L)
    public synchronized void refreshIpMatcherMapOnChange() {
        if (propertyService.changedSinceLastAccess(PRODUCER_CACHE_KEY)) {
            refreshIpMatcherMap();
        }
    }
}
