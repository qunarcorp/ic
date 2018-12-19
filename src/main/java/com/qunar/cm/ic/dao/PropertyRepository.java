package com.qunar.cm.ic.dao;

import com.qunar.cm.ic.model.Property;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Created by yu.qi on 2018/9/5.
 */
public interface PropertyRepository extends MongoRepository<Property, ObjectId> {
    Optional<Property> findByKey(String key);
}
