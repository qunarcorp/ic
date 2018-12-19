package com.qunar.cm.ic.service.impl;

import com.google.common.collect.Maps;
import com.qunar.cm.ic.dao.PropertyRepository;
import com.qunar.cm.ic.model.Property;
import com.qunar.cm.ic.service.PropertyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by yu.qi on 2018/9/5.
 */
@Service
public class PropertyServiceImpl implements PropertyService {
    @Resource
    private PropertyRepository propertyRepository;

    //下面的方法都定义成了synchronized，这里可以使用Map
    private Map<String, Long> versions = Maps.newHashMap();

    /**
     * 这个方法调用不频繁，添加synchronized不会影响性能
     */
    @Override
    public synchronized boolean changedSinceLastAccess(String key) {
        Optional<Property> optionalProperty = propertyRepository.findByKey(key);
        Property property = optionalProperty.orElseGet(() -> createVersionProperty(key));
        Long oldVersion = versions.put(property.getKey(), property.getVersion());
        return !Objects.equals(oldVersion, property.getVersion());
    }

    private synchronized Property createVersionProperty(String key) {
        Property property = new Property();
        property.setKey(key);
        property.setVersion(0L);
        return property;
    }
}
