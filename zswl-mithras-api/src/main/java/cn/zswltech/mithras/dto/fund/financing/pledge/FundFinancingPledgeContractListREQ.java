package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/20 2:42 下午
 **/
@Data
@ApiModel("融资管理-根据项目id获取合同下拉列表-请求体")
public class FundFinancingPledgeContractListREQ {

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 业务部门id
     */
    @ApiModelProperty("项目id")
    private Long projId;

    /**
     * 业务部门id
     */
    @ApiModelProperty("业务部门id")
    @NotNull(message = "业务部门id不能为空")
    private Long bizDeptId;
    

}
