package com.zmops.iot.web.product.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author nantian created at 2021/8/4 11:44
 * <p>
 * 产品标签
 */

@Data
public class ProductTag {

    @NotNull
    private String productId;

    @Valid
    private List<Tag> productTag;

    @Data
    public static class Tag {

        @Size(max = 20)
        @Pattern(regexp = "^[A-Za-z0-9]+$",
                message = "标签名称不能包含中文、特殊字符")
        private String tag;

        @Size(max = 50)
        private String value;
    }
}
