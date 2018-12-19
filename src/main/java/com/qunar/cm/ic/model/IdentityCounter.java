package com.qunar.cm.ic.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by dandan.sha on 2018/08/30.
 */
@Document(collection = "identitycounters")

public class IdentityCounter {
    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "IdentityCounter{" +
                "count=" + count +
                '}';
    }
}
