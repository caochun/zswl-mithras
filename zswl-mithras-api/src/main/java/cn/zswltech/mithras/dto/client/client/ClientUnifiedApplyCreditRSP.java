package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ClientUnifiedApplyCreditRSP {

    @ApiModelProperty("授信总额度")
    private Long applyCreditAmount;

    @ApiModelProperty("已用额度")
    private Long usedCreditAmount;

    @ApiModelProperty("未用额度")
    private Long unusedCreditAmount;

    @ApiModelProperty("使用率")
    private BigDecimal usageRate;

    /**
     * 剩余本金
     */
    @ApiModelProperty("剩余本金")
    private Long remainingPrincipal;

}
