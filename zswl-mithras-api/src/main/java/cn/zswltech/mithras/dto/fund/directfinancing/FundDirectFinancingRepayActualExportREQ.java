package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-实际还款表编辑-请求体")
public class FundDirectFinancingRepayActualExportREQ {

    @ApiModelProperty(value = "融资id")
    private Long financingId;
}
