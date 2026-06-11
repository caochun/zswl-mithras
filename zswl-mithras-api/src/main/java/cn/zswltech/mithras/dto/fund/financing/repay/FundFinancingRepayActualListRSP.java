package cn.zswltech.mithras.dto.fund.financing.repay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("融资管理-还款实际表列表-返回体")
public class FundFinancingRepayActualListRSP extends FundFinancingRepayEstimateListRSP {
    @ApiModelProperty("现金流编号")
    private String cashFlowCode;

    /**
     * 核销状态
     */
    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("确认状态")
    private Integer isConfirmed;

    @ApiModelProperty("核销状态")
    private Integer isPaid;
}
