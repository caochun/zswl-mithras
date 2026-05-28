package cn.zswltech.mithras.dto.projpricing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/3/10
 * @description
 */
@Data
public class CorpSubjectItemCheckResultDetail {
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("组织机构类型")
    private String orgType;

    @ApiModelProperty("客户在项目中承担的角色名称")
    private String projectRoleName;

    @ApiModelProperty("客户财报校验是否通过，0-不通过，1-通过")
    private Integer checkResult;

    @ApiModelProperty("财报校验未通过原因")
    private String reason;

    @ApiModelProperty("财务报表检查结果列表")
    private List<CheckResultData> checkResultDataList;

    @Data
    public static class CheckResultData {
        @ApiModelProperty("报告期")
        private String reportPeriod;

        @ApiModelProperty("资产负债表检查结果，0-缺失，1-存在，-1-无需检查")
        private Integer capitalBalanceCheckResult;

        @ApiModelProperty("利润表表检查结果，0-缺失，1-存在，-1-无需检查")
        private Integer profitCheckResult;

        @ApiModelProperty("现金流量表检查结果，0-缺失，1-存在，-1-无需检查")
        private Integer cashFlowCheckResult;

        @ApiModelProperty("收入支出表检查结果，0-缺失，1-存在，-1-无需检查")
        private Integer incomeExpendResult;

        @ApiModelProperty("检查结果，0-不通过，1-通过")
        private Integer checkResult;
    }
}
