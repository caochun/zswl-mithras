package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthBaseRSP {
    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("业务负责人id")
    private Long bizDeptLeaderId;

    @ApiModelProperty("业务负责人名称")
    private String bizDeptLeaderName;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("部门名称")
    private String belongDeptName;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区、县")
    private String district;
}
