package com.zmops.zeus.iot.server.receiver.http.predicate;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;

/**
 * @author nantian created at 2021/8/16 18:19
 */
public class HeaderPredicate implements Predicate {

    @Override
    public boolean matches(Exchange exchange) {


        return exchange.getIn().getHeader("username").equals("zhangsan");
    }
}
