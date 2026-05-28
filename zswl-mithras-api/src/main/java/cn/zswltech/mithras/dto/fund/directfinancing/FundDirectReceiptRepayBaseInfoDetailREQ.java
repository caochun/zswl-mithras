package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/21 10:21
 */
@Data
@ApiModel("直接融资收付款-基本信息-请求体")
public class FundDirectReceiptRepayBaseInfoDetailREQ {
    @ApiModelProperty(value = "直接融资id")
    @NotNull(message = "直接融资id不能为空")
    private Long financingId;
}
