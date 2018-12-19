package com.qunar.cm.ic.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qunar.cm.ic.model.jackson.EventDeserializer;
import com.qunar.cm.ic.model.jackson.EventSerializer;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * Created by dandan.sha on 2018/08/24.
 * <p>
 * 事件有3个字段在数据库中的名称和类的字段名不同，分别是：
 * event->type
 * _ip->ip
 * _hidden->hidden
 * 使用Field注解表明字段在数据库中的类型，但实际上并没有作用，因为在读写数据库时使用的是Converter完成的数据转化
 */

@Document(collection = "eventinfos")
@JsonSerialize(using = EventSerializer.class)
@JsonDeserialize(using = EventDeserializer.class)
public class Event {
    @Field("id")
    private Long id;

    @Field("event")
    private String type;
    private String operator;
    //事件来源
    private String source;
    //事件发生的时间
    private Date time;
    //事件被添加到IC的时间
    private Date updated;
    //用户定义的其它的字段，也可以包含上面定义的字段，但如果二者的值不同，则以上面定义的字段值为准
    private Map<String, Object> body;
    //事件发送者的ip
    @Field("_ip")
    private String ip;
    //是否发布事件
    @Field("_hidden")
    private Boolean hidden;
    //是否是一个假事件，用于占位
    @Field("_dummy")
    private Boolean dummy;

    public void normalizeBody() {
        body.put("timestamp", time.getTime());
        body.put("type", type);

        body.remove("updated");
        body.remove("updatedTimestamp");
        body.remove("id");
        body.remove("time");
        body.remove("event");
        body.entrySet().removeIf(next -> next.getKey().startsWith("_"));
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getDummy() {
        return dummy;
    }

    public void setDummy(Boolean dummy) {
        this.dummy = dummy;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", time=" + time +
                ", type='" + type + '\'' +
                ", body=" + body +
                '}';
    }
}
