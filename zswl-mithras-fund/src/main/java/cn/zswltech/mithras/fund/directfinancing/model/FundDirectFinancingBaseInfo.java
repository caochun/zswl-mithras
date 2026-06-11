package cn.zswltech.mithras.fund.directfinancing.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-详情信息
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundDirectFinancingBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 产品名称
    */
    @TableField("product_name")
    private String productName;

    /**
     * 承销商
     */
    @TableField("consignee")
    private String consignee;

    /**
    * 融资编号
    */
    @TableField("financing_code")
    private String financingCode;

    /**
     * 融资金额
     */
    @TableField("financing_amount")
    private Long financingAmount;

    /**
     * 发行规模
     */
    @TableField("issuing_scale")
    private Long issuingScale;

    /**
    * 项目类型
    */
    @TableField("direct_financing_type")
    private String directFinancingType;

    /**
    * 交易流通场所
    */
    @TableField("trading_venues")
    private String tradingVenues;

    /**
    * 发行方式
    */
    @TableField("issuance_method")
    private String issuanceMethod;

    /**
     * 存续时间起
     */
    @TableField("duration_from")
    private LocalDate durationFrom;

    /**
     * 存续时间止
     */
    @TableField("duration_to")
    private LocalDate durationTo;

    /**
     * 首个兑付日
     */
    @TableField("first_payment_date")
    private LocalDate firstPaymentDate;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

    /**
    * 资金经理id
    */
    @TableField("fund_manager_id")
    private Long fundManagerId;

    /**
    * 所属部门id
    */
    @TableField("dept_id")
    private Long deptId;

    /**
    * 部门负责人id
    */
    @TableField("biz_header_id")
    private Long bizHeaderId;

    /**
     * 分管领导id
     */
    @TableField("leader_id")
    private Long leaderId;

    /**
     * 票面加权平均利率
     */
    @TableField("average_coupon_rate")
    private Long averageCouponRate;

    /**
     * 作废
     */
    @TableField("obsolete")
    private Boolean obsolete;

    /**
     * 核销状态
     */
    @TableField("write_off_status")
    private String writeOffStatus;

    /**
     * 起息日
     */
    @TableField("carry_interest_time")
    private LocalDate carryInterestTime;

    /**
     * 到期日
     */
    @TableField("duration_time")
    private LocalDate durationTime;

    /**
     * 融资期限（月）
     */
    @TableField("financing_month")
    private Integer financingMonth;


    /**
     * FTP收益率
     */
    @TableField("ftp_yield_rate")
    private Integer ftpYieldRate;

    /**
     * 融资状态
     */
    @TableField("financing_status")
    private String financingStatus;

    /**
     * 综合融资成本
     */
    @TableField("comprehensive_financing_cost")
    private Long comprehensiveFinancingCost;

    /**
     * 还款方式
     */
    @TableField("repay_way")
    private String repayWay;

    /**
     * 还款频率
     */
    @TableField("repay_frequency")
    private String repayFrequency;

    /**
     * 计算日
     */
    @TableField("calculate_day")
    private Integer calculateDay;

}
