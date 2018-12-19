package com.qunar.cm.ic.dao;

import com.qunar.cm.ic.model.Type;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by dandan.sha on 2018/08/28.
 */
@Repository
public interface TypeRepository extends MongoRepository<Type, Long> {
    Optional<Type> findOneByName(String name);
}
