package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/17 14:34
 */
@Data
@ApiModel("直接融资-资产池信息详情-请求体")
public class FundDirectFinancingAssetPoolDetailREQ {
    @ApiModelProperty(value = "融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;
}
