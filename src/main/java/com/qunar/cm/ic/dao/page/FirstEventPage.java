package com.qunar.cm.ic.dao.page;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by yu.qi on 2018/08/30.
 * <p>
 * 这个类的作用主要是为了排序和限制返回的数量，而不是分页
 */
public final class FirstEventPage extends AbstractPageRequest {

    public static FirstEventPage of(int size) {
        //只返回第一页
        return new FirstEventPage(0, size);
    }

    private FirstEventPage(int page, int size) {
        super(page, size);
    }


    @Override
    public Sort getSort() {
        return Sort.by(Sort.Order.asc("id"));
    }

    @Override
    public Pageable next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable previous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
        throw new UnsupportedOperationException();
    }
}
