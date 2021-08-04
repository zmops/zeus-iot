package com.zmops.iot.model.page;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果的封装
 *
 * @author nantian
 */
@Data
public class Pager<T> {

    private Integer code = 200;

    private String msg = "请求成功";

    private List<T> data = new ArrayList<>();

    private long count = 0;

    public Pager() {
    }

    public Pager(List<T> data, long count) {
        this.data = data;
        this.count = count;
    }

    public <V> Pager(List<V> data, long count, Function<V, T> mapFunc) {
        this.data = data.stream().map(mapFunc).collect(Collectors.toList());
        this.count = count;
    }

    public static Pager empty(long total) {
        return new Pager<>(Collections.emptyList(), total);
    }

}
