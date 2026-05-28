package cn.zswltech.mithras.service.service.payment.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bigbear
 * @date 2024/9/11 20:57
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicInfoExcelModel {

    @ApiModelProperty(value = "确认人名称")
    private String confirmedName;

    @ApiModelProperty(value = "确认时间")
    private String confirmedTime;

    @ApiModelProperty(value = "查询时间开始")
    private String queryFrom;

    @ApiModelProperty(value = "查询时间结束")
    private String queryTo;

    @ApiModelProperty(value = "承租人/担保人名称")
    private String tenantryName;

    @ApiModelProperty(value = "企查查-基本信息-变更记录-结果枚举")
    private String QCC_CHANGE_RECORD_ENUM;

    @ApiModelProperty(value = "企查查-基本信息-变更记录-运营经办")
    private String QCC_CHANGE_RECORD_YY;

    @ApiModelProperty(value = "企查查-基本信息-变更记录-项目经理")
    private String QCC_CHANGE_RECORD_XMJL;

    @ApiModelProperty(value = "企查查-基本信息-股权穿透图-枚举")
    private String QCC_EQUITY_PENETRATION_ENUM;

    @ApiModelProperty(value = "企查查-基本信息-股权穿透图-运营经办")
    private String QCC_EQUITY_PENETRATION_YY;

    @ApiModelProperty(value = "企查查-基本信息-股权穿透图-项目经理")
    private String QCC_EQUITY_PENETRATION_XMJL;

    @ApiModelProperty(value = "企查查-经营风险-枚举")
    private String QCC_OPERATING_RISK_ENUM;

    @ApiModelProperty(value = "企查查-经营风险-运营经办")
    private String QCC_OPERATING_RISK_YY;

    @ApiModelProperty(value = "企查查-经营风险-项目经理")
    private String QCC_OPERATING_RISK_XMJL;

    @ApiModelProperty(value = "企查查-法律诉讼-枚举")
    private String QCC_LEGAL_LITIGATION_ENUM;

    @ApiModelProperty(value = "企查查-法律诉讼-运营经办")
    private String QCC_LEGAL_LITIGATION_YY;

    @ApiModelProperty(value = "企查查-法律诉讼-项目经理")
    private String QCC_LEGAL_LITIGATION_XMJL;

    @ApiModelProperty(value = "企查查-关联风险-枚举")
    private String QCC_ASSOCIATION_RISK_ENUM;

    @ApiModelProperty(value = "企查查-关联风险-运营经办")
    private String QCC_ASSOCIATION_RISK_YY;

    @ApiModelProperty(value = "企查查-关联风险-项目经理")
    private String QCC_ASSOCIATION_RISK_XMJL;

    @ApiModelProperty(value = "企查查-债券违约-枚举")
    private String QYYJT_BOND_DEFAULT_ENUM;

    @ApiModelProperty(value = "企查查-债券违约-运营经办")
    private String QYYJT_BOND_DEFAULT_YY;

    @ApiModelProperty(value = "企查查-债券违约-项目经理")
    private String QYYJT_BOND_DEFAULT_XMJL;

    @ApiModelProperty(value = "中登网-登记详细信息列表-枚举")
    private String ZDW_REGISTRATION_DETAILS_ENUM;

    @ApiModelProperty(value = "中登网-登记详细信息列表-运营经办")
    private String ZDW_REGISTRATION_DETAILS_YY;

    @ApiModelProperty(value = "中登网-登记详细信息列表-项目经理")
    private String ZDW_REGISTRATION_DETAILS_XMJL;

    @ApiModelProperty(value = "全国法院被执行人或被纳入失信人查询\n【适用于担保自然人】-枚举")
    private String QG_COURT_EXECUTIONER_ENUM;

    @ApiModelProperty(value = "全国法院被执行人或被纳入失信人查询\n【适用于担保自然人】-运营经办")
    private String QG_COURT_EXECUTIONER_YY;

    @ApiModelProperty(value = "全国法院被执行人或被纳入失信人查询\n【适用于担保自然人】-项目经理")
    private String QG_COURT_EXECUTIONER_XMJL;

    @ApiModelProperty(value = "征信报告-枚举")
    private String CREDIT_REPORT_ENUM;

    @ApiModelProperty(value = "征信报告-运营经办")
    private String CREDIT_REPORT_YY;

    @ApiModelProperty(value = "征信报告-项目经理")
    private String CREDIT_REPORT_XMJL;

    @ApiModelProperty(value = "其他补充-枚举")
    private String OTHER_SUPPLEMENTARY_ENUM;

    @ApiModelProperty(value = "其他补充-运营经办")
    private String OTHER_SUPPLEMENTARY_YY;

    @ApiModelProperty(value = "其他补充-项目经理")
    private String OTHER_SUPPLEMENTARY_XMJL;

}
