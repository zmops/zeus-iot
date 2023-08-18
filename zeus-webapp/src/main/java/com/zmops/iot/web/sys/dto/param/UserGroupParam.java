/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zmops.iot.web.sys.dto.param;

import com.zmops.iot.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户组参数
 *
 * @author yefei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupParam extends BaseQueryParam {

    private String groupName;

    private String status;

    @NotNull
    private List<Long> userGroupIds;

    @NotNull(groups = BaseEntity.BindDevice.class)
    private Long userGroupId;

    @NotNull(groups = BaseEntity.BindDevice.class)
    private List<Long> deviceGroupIds;
}
