package com.qunar.cm.ic.dao.converter;

import com.google.common.collect.Maps;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.model.Event;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by dandan.sha on 2018/08/24.
 */

@ReadingConverter
@Component
public class EventReadConverter implements Converter<Document, Event> {
    @Nullable
    @Override
    public Event convert(Document source) {
        Event event = new Event();
        event.setId(parseId(source));
        event.setTime(source.getDate("time"));
        event.setUpdated(source.getDate("updated"));
        event.setOperator(source.getString("operator"));
        event.setSource(source.getString("source"));
        //数据库中存储的字段为event，展示给用户的是type
        event.setType(source.getString("event"));
        event.setIp(source.getString("_ip"));
        //可选字段，旧的数据没有这个字段
        event.setHidden(source.get("_hidden", false));
        event.setHidden(source.get("_dummy", false));
        event.setBody(Maps.newLinkedHashMap(source));
        event.normalizeBody();
        return event;
    }

    /**
     * 解析并将id转换成long类型,同时支持int和long两种格式
     */
    private long parseId(Document source) {
        Object id = source.get("id");
        if (id instanceof Integer) {
            return ((Integer) id).longValue();
        } else if (id instanceof Long) {
            return (long) id;
        } else {
            throw new ICException(ExceptionEnum.DATA_CONVERTER_ERROR, source.toJson());
        }
    }
}
