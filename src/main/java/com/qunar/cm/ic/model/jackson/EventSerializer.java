package com.qunar.cm.ic.model.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.qunar.cm.ic.model.Event;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by yu.qi on 2018/08/27.
 */
public class EventSerializer extends JsonSerializer<Event> {
    private static final Set<String> reservedFieldNames = Sets.newHashSet(
            "id", "type", "event", "operator", "source", "time",
            "timestamp", "updated", "updatedTimestamp"
    );
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    @Override
    public void serialize(Event event, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", event.getId());
        jsonGenerator.writeStringField("type", event.getType());
        jsonGenerator.writeStringField("event", event.getType());
        jsonGenerator.writeStringField("operator", event.getOperator());
        jsonGenerator.writeStringField("source", event.getSource());
        jsonGenerator.writeStringField("time", formatDateTime(event.getTime()));
        jsonGenerator.writeNumberField("timestamp", event.getTime().getTime());
        jsonGenerator.writeStringField("updated", formatDateTime(event.getUpdated()));
        jsonGenerator.writeNumberField("updatedTimestamp", event.getUpdated().getTime());
        for (Map.Entry<String, Object> entry : event.getBody().entrySet()) {
            if (!entry.getKey().startsWith("_") && !reservedFieldNames.contains(entry.getKey())) {
                jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
            }
        }
        jsonGenerator.writeEndObject();
    }

    String formatDateTime(Date date) {
        Preconditions.checkNotNull(date);
        //调用truncatedTo是为了去掉毫秒，最终生成的格式如2018-08-28T14:28:21+08:00
        OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC+8"));
        return offsetDateTime.format(formatter);
    }
}
