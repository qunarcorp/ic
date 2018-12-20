package com.qunar.cm.ic.service.impl;

import com.qunar.cm.ic.service.EventConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created by yu.qi on 2018/08/31.
 */
@Service
public class EventConsumerServiceImpl implements EventConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumerServiceImpl.class);


    @Override
    public long waitForNextEvent(String token, Date deadline) throws TimeoutException {
        if (new Date().after(deadline)) {
            throw new TimeoutException();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.error("Listener[token={}]消费事件时，等待事件失败", token, e);
        }
        return 0;
    }

}
