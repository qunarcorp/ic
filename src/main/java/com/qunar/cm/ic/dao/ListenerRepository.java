package com.qunar.cm.ic.dao;

import com.qunar.cm.ic.model.Listener;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Created by yu.qi on 2018/08/29.
 */
public interface ListenerRepository extends MongoRepository<Listener, ObjectId> {
    Optional<Listener> findByToken(String token);
}
