package com.qunar.cm.ic.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.mongodb.client.result.UpdateResult;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.dao.EventRepository;
import com.qunar.cm.ic.model.Event;
import com.qunar.cm.ic.model.IdentityCounter;
import com.qunar.cm.ic.service.EventService;
import com.qunar.cm.ic.service.TypeService;
import joptsimple.internal.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.Resource;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by dandan.sha on 2018/08/24.
 */

@Service
public class EventServiceImpl implements EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L;
    private static final Sort orderByIdAsc = Sort.by(Sort.Order.asc("id"));


    @Resource
    private EventRepository eventRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private TypeService typeService;


    private LoadingCache<Long, Event> caches;

    private AtomicLong eventCount = new AtomicLong();

    private volatile boolean running = true;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public EventServiceImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            logger.info("程序即将退出，publishEvents定时任务将终止");
        }));
        caches = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .recordStats()
                .build(new CacheLoader<Long, Event>() {
                    @Override
                    @ParametersAreNonnullByDefault
                    public Event load(Long key) throws Exception {
                        Event event = queryByIdFromDb(key);
                        logger.info("从数据库中加载事件{}到缓存中，添加前缓存大小为{}", key, caches.size());
                        return event;
                    }
                });
    }


    @Override
    public Event queryById(Long id) {
        //使用getUnchecked要求CacheLoader.load方法必须不能抛出任何checked的异常
        try {
            return caches.getUnchecked(id);
        } catch (UncheckedExecutionException e) {
            //如果load方法出现异常，取出原始的ICException异常对象
            if (e.getCause() instanceof ICException) {
                throw (ICException) e.getCause();
            }
            throw e;
        }
    }

    private Event queryByIdFromDb(Long id) {
        Optional<Event> optionalEvent = eventRepository.findOneById(id);
        return optionalEvent.orElseThrow(() ->
                new ICException(ExceptionEnum.PARAMS_INVALID, "事件" + id + "不存在"));
    }

    @Override
    public Event checkAndSaveEvent(Event event) {
        typeService.checkEvent(event);
        IdentityCounter identityCounter = getIdentityCounter();
        event.setId(identityCounter.getCount());
        eventRepository.insert(event);
        notifyForInsertedEvent();
        return event;
    }

    private void notifyForInsertedEvent() {
        eventCount.incrementAndGet();
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private IdentityCounter getIdentityCounter() {
        Query query = new Query(Criteria.where("field").is("id"));
        Update update = new Update();
        update.inc("count", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        IdentityCounter identityCounter = mongoTemplate.findAndModify(query, update, options, IdentityCounter.class);
        assert identityCounter != null;
        return identityCounter;
    }

    @Override
    public List<Event> queryByTimeAndType(String type, String from, String to) {
        Date fromDate = parseDate(from);
        Date toDate;
        if (Strings.isNullOrEmpty(to)) {
            toDate = new Date();
        } else {
            toDate = parseDate(to);
        }
        if (toDate.getTime() - fromDate.getTime() > MILLISECONDS_PER_DAY) {
            throw new ICException(ExceptionEnum.PARAMS_INVALID, "from和to时间跨度不能超过一天");
        }
        return eventRepository.findByTypeAndTime(type, fromDate, toDate);
    }

    private Date parseDate(String from) {
        try {
            return Date.from(OffsetDateTime.parse(from).toInstant());
        } catch (DateTimeParseException e) {
            throw new ICException(ExceptionEnum.PARAMS_INVALID, "时间格式" + from + "不合法", e);
        }
    }


    @Scheduled(fixedDelay = 1000L)
    public synchronized void publishEvents() {
        logger.info("publishEvents定时任务执行开始");
        Long lastNotHiddenEventId = getLastNotHiddenEventId();

        Long oldEventCount = 0L;
        Long newEventCount = eventCount.get();

        NewEventFoundTime newEventFoundTime = new NewEventFoundTime();
        //单位秒
        while (running) {
            while (Objects.equals(oldEventCount, newEventCount)) {
                lock.lock();
                try {
                    if (!condition.await(5, TimeUnit.SECONDS)) {
                        break;
                    }
                } catch (InterruptedException e) {
                    logger.error("publishEvents定时任务被中断", e);
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
                newEventCount = eventCount.get();
            }
            oldEventCount = newEventCount;

            List<Event> hiddenEvents = eventRepository.findGreaterThanId(lastNotHiddenEventId, orderByIdAsc);
            List<Event> sequentialEvents = getSequentialEvents(lastNotHiddenEventId, hiddenEvents);
            logger.info("publishEvents定时任务获取到{}个隐藏的事件，其中包含{}个连续事件，lastNotHiddenEventId为{}",
                    hiddenEvents.size(), sequentialEvents.size(), lastNotHiddenEventId);

            //处理等待超时的逻辑
            if (sequentialEvents.isEmpty()) {
                if (!hiddenEvents.isEmpty()) {
                    newEventFoundTime.set();
                    if (newEventFoundTime.timeout()) {
                        createDummyEvent(++lastNotHiddenEventId);
                        logger.warn("publishEvents定时任务没有找到事件{}且已经超时，已经跳过该事件", lastNotHiddenEventId);
                        newEventFoundTime.unset();
                    }
                }
            } else {
                newEventFoundTime.unset();
                publishEvents(sequentialEvents);
                lastNotHiddenEventId += sequentialEvents.size();
            }
        }
        logger.info("publishEvents定时任务执行结束");
    }

    private void publishEvents(List<Event> sequentialEvents) {
        Preconditions.checkState(!sequentialEvents.isEmpty());
        //发布事件，先将事件hidden改为false,然后发qmq消息
        Long firstId = sequentialEvents.get(0).getId();
        Long lastId = sequentialEvents.get(sequentialEvents.size() - 1).getId();
        Query query = Query.query(Criteria.where("id").gte(firstId).lte(lastId));
        Update update = Update.update("_hidden", false);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Event.class);
        Preconditions.checkState(updateResult.wasAcknowledged());
        //getMatchedCount返回的是long，size返回的是int，不能使用equals
        Preconditions.checkState(updateResult.getMatchedCount() == sequentialEvents.size());
        //TODO
        logger.info("publishEvents定时任务发布了事件{}",
                sequentialEvents.stream().map(Event::getId).collect(Collectors.toList()));
    }

    private List<Event> getSequentialEvents(Long startEventId, List<Event> events) {
        List<Event> sequentialEvents = Lists.newArrayList();
        Long lastEventId = startEventId;
        for (Event event : events) {
            if (!Objects.equals(event.getId(), ++lastEventId)) {
                break;
            }
            sequentialEvents.add(event);
        }
        return sequentialEvents;
    }

    private void createDummyEvent(Long id) {
        Event dummyEvent = new Event();
        dummyEvent.setId(id);
        dummyEvent.setHidden(false);
        dummyEvent.setDummy(true);
        //这里不能使用save方法，因为save方法对已经存在的id会直接执行更新操作
        eventRepository.insert(dummyEvent);
        notifyForInsertedEvent();
    }

    private Long getLastNotHiddenEventId() {
        //因为_hidden可能为null,所以使用ne(true)
        Query query = new Query(Criteria.where("_hidden").ne(true));
        query.with(Sort.by(Sort.Order.desc("id"))).limit(1); //按id进行 降序
        Event event = mongoTemplate.findOne(query, Event.class);
        if (event != null) {
            return event.getId();
        } else {
            return 0L;
        }
    }

    private static class NewEventFoundTime {
        //单位为毫秒
        private static long newEventFoundTimeout = 5000;
        private Date date;

        private void set() {
            if (date == null) {
                date = new Date();
            }
        }

        private void unset() {
            date = null;
        }

        private boolean timeout() {
            Preconditions.checkNotNull(date, "必须先调用set方法");
            return new Date().getTime() - date.getTime() > newEventFoundTimeout;
        }
    }
}
