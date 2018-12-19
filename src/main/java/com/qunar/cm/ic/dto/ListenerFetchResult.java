package com.qunar.cm.ic.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qunar.cm.ic.model.Event;

import java.util.List;

/**
 * Created by yu.qi on 2018/08/29.
 */
public class ListenerFetchResult {
    private String code;
    private List<Event> list;
    private Integer maxResults;
    private Boolean hasMore;
    @JsonIgnore
    private boolean filled;

    public boolean getFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public ListenerFetchResult(String code, Integer maxResults) {
        this.code = code;
        this.maxResults = maxResults;
    }

    public String getCode() {
        return code;
    }


    public List<Event> getList() {
        return list;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public void setList(List<Event> list) {
        this.list = list;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}
