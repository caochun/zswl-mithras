package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 14:17
 */
@ApiModel("直接融资-详情信息-请求体")
@Data
public class FundDirectFinancingSingleIdREQ {
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;
}
