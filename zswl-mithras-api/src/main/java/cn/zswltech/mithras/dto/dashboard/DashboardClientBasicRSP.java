package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardClientBasicRSP {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户分类")
    private String clientType;
    @ApiModelProperty("客户分类名称")
    private String clientTypeName;
    private Long createBy;
    private String creatorName;
    @ApiModelProperty("风控行业分类code RiskControlIndustryClassify")
    private String riskControlIndustryClassifyCode;
    @ApiModelProperty("风控行业分类display")
    private String riskControlIndustryClassifyDisplay;
    @ApiModelProperty("资产五级分类code")
    private String assetClassifyResultCode;
    @ApiModelProperty("资产五级分类display")
    private String assetClassifyResultDisplay;
    @ApiModelProperty("国标行业分类code")
    private String industryTypeCode;
    @ApiModelProperty("国标行业分类display")
    private String industryTypeDisplay;
    private List<String> industryTypeDisplayCodeList;
    @ApiModelProperty("企业性质code")
    private String enterpriseNatureCode;
    @ApiModelProperty("企业性质display")
    private String enterpriseNatureDisplay;
    @ApiModelProperty("省份code")
    private String provinceCode;
    @ApiModelProperty("省份display")
    private String provinceDisplay;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("项目协办ID，多个用英文逗号分隔")
    private String projCosponsorUserIds;
    @ApiModelProperty("项目协办名称，多个用英文逗号分隔")
    private String projCosponsorUserNames;
    @ApiModelProperty("评级结果（预留）")
    private String gradeResult;
    @ApiModelProperty("项目阶段")
    private String projectStage;
    @ApiModelProperty("合同编号")
    private String contractCodes;
    private String projNames;
    private Set<String> projSet;
}
