package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 融资成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("融资成本定价刷新-请求体")
public class NewFtpFinancingCostPricingFlashREQ {

    /**
     * mainId
     */
    @ApiModelProperty(value = "mainId")
    private Long mainId;
}
