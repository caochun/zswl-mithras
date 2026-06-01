package cn.zswltech.mithras.service.mapper.model.kpi;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 业绩基本信息表
 * @author yangxiong
 * @TableName performance_base_info
 */
@TableName(value ="performance_base_info")
@Data
public class PerformanceBaseInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Long year;

    /**
     * 主表ID
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 所属部门ID
     */
    @TableField(value = "belong_dept_id")
    private Long belongDeptId;

    /**
     * 所属部门name
     */
    @TableField(value = "belong_dept_name")
    private String belongDeptName;

    /**
     * 业务人员ID
     */
    @TableField(value = "business_user_id")
    private Long businessUserId;

    /**
     * 业务类型(枚举)
     */
    @TableField(value = "business_type")
    private String businessType;

    /**
     * 所属类型(枚举)，标识种类
     */
    @TableField(value = "belong_type")
    private String belongType;

    /**
     * 资产余额目标
     */
    @TableField(value = "asset_balance_target")
    private Long assetBalanceTarget;

    /**
     * 投放额目标
     */
    @TableField(value = "advertising_amount")
    private Long advertisingAmount;

    /**
     * 营业收入目标
     */
    @TableField(value = "revenue_target")
    private Long revenueTarget;

    /**
     * 利润目标
     */
    @TableField(value = "profit_target")
    private Long profitTarget;

    /**
     * 咨询服务费收入
     */
    @TableField(value = "consulting_fee_income")
    private Long consultingFeeIncome;

    /**
     * 利息收入
     */
    @TableField(value = "interest_income")
    private Long interestIncome;

    /**
     * 经营费用
     */
    @TableField(value = "biz_fee")
    private Long bizFee;

    /**
     * 差旅费
     */
    @TableField(value = "business_trip_fee")
    private Long businessTripFee;

    /**
     * 业务招待费
     */
    @TableField(value = "business_serve_fee")
    private Long businessServeFee;

    /**
     * 拨备前利润目标
     */
    @TableField(value = "before_profit_target")
    private Long beforeProfitTarget;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}