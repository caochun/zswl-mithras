package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bigbear
 * @date 2025/3/21 14:52
 * @description
 */
@Data
@ApiModel(value = "合同评级机构列表-返回参数")
public class ContractEvaluationAgencyListRSP {

    @ApiModelProperty(value = "评估机构id")
    private Long companyId;

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    /**
     * {@link cn.zswltech.mithras.service.enums.lease.LeaseAppraisalPurposeEnum#name()}
     */
    @ApiModelProperty(value = "用途")
    private String purpose;

    /**
     * {@link cn.zswltech.mithras.service.enums.lease.LeaseAppraisalSelectEnum#name()}
     */
    @ApiModelProperty(value = "是否被选中")
    private String selectType;
}
