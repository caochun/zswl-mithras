package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


/**
 * AccountBalanceListREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "监管户待转资金当日请求参数")
public class FundTransferAccountCurrentDailyREQ extends PageReq {
    /**
     * 当日情况
     */
    @ApiModelProperty(value = "当日情况")
    private LocalDate currentDate;
    /**
     * 银行名称
     */
    @ApiModelProperty(value = "银行名称")
    private String accountBank;
    /**
     * 沉淀时间
     */
    @ApiModelProperty(value = "沉淀时间")
    private String settingTime;
}
