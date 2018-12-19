package com.qunar.cm.ic.service;

import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created by yu.qi on 2018/08/31.
 */
public interface EventConsumerService {

    long waitForNextEvent(String token, Date deadline) throws TimeoutException;
}
