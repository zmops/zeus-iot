package com.zmops.iot.web.product.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author nantian created at 2021/8/4 11:44
 * <p>
 * 产品标签
 */

@Data
public class ProductTag {

    @NotNull
    private Long productId;

    private List<Tag> productTag;

    @Data
    public static class Tag {

        @Max(20)
        private String tag;

        @Max(50)
        private String value;
    }
}
