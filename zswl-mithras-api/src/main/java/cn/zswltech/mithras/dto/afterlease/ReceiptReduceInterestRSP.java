package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 通知财务系统收款
 * @author jackerhe
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("罚息剩余金额")
@Data
@Builder
public class ReceiptReduceInterestRSP {

    @ApiModelProperty("剩余罚息")
    private Long penaltyInterestSurplusAmount;

}
