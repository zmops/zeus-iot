package com.zmops.iot.core.auth.util;

import java.util.LinkedList;

/**
 * @author yefei
 **/
public class FixLengthLinkedList<T> extends LinkedList<T> {

    private int capacity;

    public FixLengthLinkedList(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public boolean add(T t) {
        if (size() + 1 > capacity) {
            super.removeFirst();
        }
        return super.add(t);
    }

    public static void main(String[] args) {
        FixLengthLinkedList<String> linkedList = new FixLengthLinkedList<>(3);
        for (int i = 0; i < 100; i++) {
            linkedList.add(i + "");
        }

        linkedList.forEach(l -> System.out.println(l));
    }
}
