package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/7/2/22:09
 * @description
 */
@Data
public class KpiPerformanceManageListRSP {

    @ApiModelProperty(value = "公司列表")
    private List<CompanyListVO> companyListVOList;

    @ApiModelProperty(value = "部门列表")
    private List<DeptListVO> deptListVOList;

    @ApiModelProperty(value = "个人列表")
    private List<PersonalListVO> personalListVOList;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class CompanyListVO extends BaseListVO {

        /**
         * 业务类型
         */
        @ApiModelProperty("业务类型")
        private String businessType;

        /**
         * 资产余额目标（万元）
         */
        @ApiModelProperty("资产余额目标（万元）")
        private String assetBalanceTarget;

        /**
         * 投放额目标（万元）
         */
        @ApiModelProperty("投放额目标（万元）")
        private String investmentTarget;

        /**
         * 营业收入目标（万元）
         */
        @ApiModelProperty("营业收入目标（万元）")
        private String revenueTarget;

        /**
         * 利润目标（万元）
         */
        @ApiModelProperty("利润目标（万元）")
        private String profitTarget;

        /**
         * 咨询服务费收入
         */
        @ApiModelProperty("咨询服务费收入（万元）")
        private String consultingFeeIncome;

        /**
         * 利息收入
         */
        @ApiModelProperty("利息收入（万元）")
        private String interestIncome;

        /**
         * 经营费用
         */
        @ApiModelProperty("经营费用（万元）")
        private String bizFee;

        /**
         * 差旅费
         */
        @ApiModelProperty("差旅费（万元）")
        private String businessTripFee;

        /**
         * 业务招待费
         */
        @ApiModelProperty("业务招待费（万元）")
        private String businessServeFee;

        /**
         * 拨备前利润目标
         */
        @ApiModelProperty("拨备前利润目标（万元）")
        private String beforeProfitTarget;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DeptListVO extends BaseListVO {

        /**
         * 部门
         */
        @ApiModelProperty("部门")
        private String dept;

        /**
         * 业务类型
         */
        @ApiModelProperty("业务类型")
        private String businessType;

        /**
         * 投放额目标（万元）
         */
        @ApiModelProperty("投放额目标（万元）")
        private String investmentTarget;

        /**
         * 营业收入目标（万元）
         */
        @ApiModelProperty("营业收入目标（万元）")
        private String revenueTarget;

        /**
         * 利润目标（万元）
         */
        @ApiModelProperty("利润目标（万元）")
        private String profitTarget;

        /**
         * 余额目标（万元）
         */
        @ApiModelProperty("余额目标（万元）")
        private String assetBalanceTarget;

        /**
         * 咨询服务费收入
         */
        @ApiModelProperty("咨询服务费收入（万元）")
        private String consultingFeeIncome;

        /**
         * 利息收入
         */
        @ApiModelProperty("利息收入（万元）")
        private String interestIncome;

        /**
         * 经营费用
         */
        @ApiModelProperty("经营费用（万元）")
        private String bizFee;

        /**
         * 差旅费
         */
        @ApiModelProperty("差旅费（万元）")
        private String businessTripFee;

        /**
         * 业务招待费
         */
        @ApiModelProperty("业务招待费（万元）")
        private String businessServeFee;

        /**
         * 拨备前利润目标
         */
        @ApiModelProperty("拨备前利润目标（万元）")
        private String beforeProfitTarget;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PersonalListVO extends BaseListVO {

        /**
         * 登陆账户名称
         */
        @ApiModelProperty("登陆账户名称")
        private String loginAccountName;

        /**
         * 业务人员名称
         */
        @ApiModelProperty("业务人员名称")
        private String staffName;

        /**
         * 所属部门
         */
        @ApiModelProperty("所属部门")
        private String dept;

        /**
         * 业务类型
         */
        @ApiModelProperty("业务类型")
        private String businessType;

        /**
         * 余额目标（万元）
         */
        @ApiModelProperty("余额目标（万元）")
        private String assetBalanceTarget;

        /**
         * 投放额目标（万元）
         */
        @ApiModelProperty("投放额目标（万元）")
        private String investmentTarget;

        /**
         * 营业收入目标（万元）
         */
        @ApiModelProperty("营业收入目标（万元）")
        private String revenueTarget;

        /**
         * 利润目标（万元）
         */
        @ApiModelProperty("利润目标（万元）")
        private String profitTarget;
    }

    @Data
    public static class BaseListVO {
        @ApiModelProperty(value = "年度")
        private Long year;

        @ApiModelProperty("1月")
        private String januaryTarget;

        @ApiModelProperty("2月")
        private String februaryTarget;

        @ApiModelProperty("3月")
        private String marchTarget;

        @ApiModelProperty("4月")
        private String aprilTarget;

        @ApiModelProperty("5月")
        private String mayTarget;

        @ApiModelProperty("6月")
        private String juneTarget;

        @ApiModelProperty("7月")
        private String julyTarget;

        @ApiModelProperty("8月")
        private String augustTarget;

        @ApiModelProperty("9月")
        private String septemberTarget;

        @ApiModelProperty("10月")
        private String octoberTarget;

        @ApiModelProperty("11月")
        private String novemberTarget;

        @ApiModelProperty("12月")
        private String decemberTarget;
    }
}
