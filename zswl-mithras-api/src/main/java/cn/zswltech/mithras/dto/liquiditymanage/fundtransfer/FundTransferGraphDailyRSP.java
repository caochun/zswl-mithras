package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * AccountBalanceListRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "监管户待转资金明细图表请求参数")
public class FundTransferGraphDailyRSP {


    @ApiModelProperty(value = "列表-以账户和日期为维度")
    private List<DepositedAmountDetail> list;

    @ApiModelProperty(value = "总额")
    private Long sum;
}
