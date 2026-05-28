package cn.zswltech.mithras.dto.liquiditymanage.fundTransfer;

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
public class FundTransferCurrentDailyREQ {
    /**
     * 当日情况
     */
    @ApiModelProperty(value = "当日情况")
    private LocalDate currentDate;
}
