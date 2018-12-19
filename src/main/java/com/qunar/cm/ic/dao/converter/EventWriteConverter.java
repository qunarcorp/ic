package com.qunar.cm.ic.dao.converter;

import com.qunar.cm.ic.model.Event;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by yu.qi on 2018/08/27.
 */
@WritingConverter
@Component
public class EventWriteConverter implements Converter<Event, Document> {

    @Nullable
    @Override
    public Document convert(Event event) {
        if (Objects.equals(event.getDummy(), true)) {
            return convertDummy(event);
        }
        Document document = new Document(event.getBody());

        document.remove("type");
        document.remove("timestamp");
        document.remove("updatedTimestamp");
        document.entrySet().removeIf(entry -> entry.getKey().startsWith("_"));

        document.put("id", event.getId());
        document.put("operator", event.getOperator());
        document.put("source", event.getSource());
        document.put("time", event.getTime());
        document.put("updated", event.getUpdated());
        document.put("_ip", event.getIp());
        document.put("_hidden", event.getHidden());
        document.put("_dummy", event.getDummy());
        //数据库中存储的字段为event，展示给用户的是type
        document.put("event", event.getType());
        return document;
    }

    private Document convertDummy(Event event) {
        Document document = new Document();
        document.put("_hidden", event.getHidden());
        document.put("_dummy", true);
        document.put("id", event.getId());
        return document;
    }
}
