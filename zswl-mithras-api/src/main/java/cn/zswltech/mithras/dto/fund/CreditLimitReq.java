package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/20 09:55
 */
@ApiModel("授信额度请求体")
@Data
public class CreditLimitReq {
    @ApiModelProperty("机构id")
    @NotNull(message = "机构id不能为空")
    private Long organizationId;
}
