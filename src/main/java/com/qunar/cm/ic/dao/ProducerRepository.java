package com.qunar.cm.ic.dao;

import com.qunar.cm.ic.model.Producer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by yu.qi on 2018/09/11.
 */
public interface ProducerRepository extends MongoRepository<Producer, ObjectId> {
}
