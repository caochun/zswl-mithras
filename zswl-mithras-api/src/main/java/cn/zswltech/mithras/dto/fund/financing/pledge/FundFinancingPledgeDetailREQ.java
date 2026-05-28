package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/20 2:57 下午
 **/
@Data
@ApiModel("融资管理-列表-请求体")
public class FundFinancingPledgeDetailREQ {

    /**
     * 质押id
     */
    @ApiModelProperty("质押id")
    @NotNull(message = "质押id不能为空")
    private Long pledgeId;

}
