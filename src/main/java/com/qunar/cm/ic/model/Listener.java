package com.qunar.cm.ic.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Created by yu.qi on 2018/08/29.
 */

@Document(collection = "listenerinfos")
public class Listener {
    //这个字段是必须的，在使用save方法时会使用这个字段的值判断是修改还是更新
    private ObjectId id;
    private String token;
    private String code;
    //token的描述信息
    private String detail;
    //true表示是用户直接通过接口访问添加的，false表示是我们提前在数据库中添加的token
    private Boolean temporary;
    //表示消费时要从这个事件开始，不包括这个事件本身
    private Long lastEventId;
    //表示上次用户获取到的最后一个事件
    private Long recordEventId;
    //最后一次consume的时间
    private Date readTime;
    //最后一次ack的时间
    private Date replyTime;
    private List<String> ips;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public Long getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(Long lastEventId) {
        this.lastEventId = lastEventId;
    }

    public Long getRecordEventId() {
        return recordEventId;
    }

    public void setRecordEventId(Long recordEventId) {
        this.recordEventId = recordEventId;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }
}
