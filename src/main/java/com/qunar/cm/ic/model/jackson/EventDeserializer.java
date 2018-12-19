package com.qunar.cm.ic.model.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.qunar.cm.ic.common.exception.ExceptionEnum;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.model.Event;
import joptsimple.internal.Strings;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yu.qi on 2018/08/27.
 */
public class EventDeserializer extends JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Event event = new Event();
        Map<String, Object> body = readBody(jp);
        event.setBody(body);
        event.setType(parseType(body));
        event.setTime(parseTime(body));
        event.setOperator((String) body.get("operator"));
        event.setSource((String) body.get("source"));
        event.setHidden(true);
        event.setDummy(false);
        event.setUpdated(new Date());
        event.normalizeBody();
        return event;
    }


    @SuppressWarnings("unchecked")
    private Map<String, Object> readBody(JsonParser jp) throws IOException {
        return jp.getCodec().readValue(jp, LinkedHashMap.class);
    }

    private Date parseTime(Map<String, Object> properties) {
        return parseTime((String) properties.get("time"), (Long) properties.get("timestamp"));
    }

    private String parseType(Map<String, Object> properties) {
        return parseType((String) properties.get("event"), (String) properties.get("type"));
    }

    /**
     * 如果事件不包含类型信息，则返回空字符串
     */
    private String parseType(String type, String event) {
        String result = Strings.EMPTY;
        if (type != null) {
            result = type;
        } else if (event != null) {
            result = event;
        }
        return result;
    }

    Date parseTime(String time, Long timestamp) {
        Date result;
        if (timestamp != null) {
            result = new Date(timestamp);
        } else if (time != null) {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(time);
            result = Date.from(offsetDateTime.toInstant());
        } else {
            throw new ICException(ExceptionEnum.PARAMS_INVALID, "事件中必须包含timestamp字段");
        }
        return result;
    }

}
