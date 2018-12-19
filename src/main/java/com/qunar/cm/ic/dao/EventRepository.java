package com.qunar.cm.ic.dao;

import com.qunar.cm.ic.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by dandan.sha on 2018/08/24.
 */

public interface EventRepository extends MongoRepository<Event, Long> {
    Optional<Event> findFirstByOrderByIdDesc();

    @Query("{_hidden: {$ne: true}, _dummy: {$ne: true}, id: {$gt: ?0}, event: {$in: ?1}}")
    List<Event> consumeEventByTypes(Long id, List<String> types, Pageable pageable);

    @Query("{_hidden: {$ne: true}, _dummy: {$ne: true}, id: {$gt: ?0}}")
    List<Event> consumeEvent(Long id, Pageable pageable);

    @Query("{id: {$gt: ?0}}")
    List<Event> findGreaterThanId(Long id, Sort sort);

    @Query("{_hidden: {$ne: true}, _dummy: {$ne: true}, id: ?0}")
    Optional<Event> findOneById(Long id);

    @Query("{_hidden: {$ne: true}, _dummy: {$ne: true}, event: ?0, time: {$gte: ?1, $lt: ?2}}")
    List<Event> findByTypeAndTime(String type, Date from, Date to);
}
