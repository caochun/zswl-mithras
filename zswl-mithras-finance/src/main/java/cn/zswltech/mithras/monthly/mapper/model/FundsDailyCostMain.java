package cn.zswltech.mithras.monthly.mapper.model;

import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/11/4
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("funds_daily_cost_main")
public class FundsDailyCostMain extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 查询key
     */
    @TableField("query_key")
    private String queryKey;

    /**
     * 融资类型
     */
    @TableField("financing_type")
    private String financingType;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;

    /**
     * 融资渠道
     */
    @TableField("financing_channel")
    private String financingChannel;

    /**
     * 起息日
     */
    @TableField("carry_interest_date")
    private LocalDate carryInterestDate;

    /**
     * 关联合同业务类型
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 关联合同租赁类型
     */
    @TableField("lease_type")
    private String leaseType;

    /**
     * 借款性质
     */
    @TableField("loan_property")
    private String loanProperty;

    /**
     * 利率
     */
    @TableField("financing_rate")
    private Integer financingRate;

    /**
     * 当年累计计提资金成本
     */
    @TableField("total_capital_cost_this_year")
    private Long totalCapitalCostThisYear;

    /**
     * 当期累计计提资金成本
     */
    @TableField("total_capital_cost_this_month")
    private Long totalCapitalCostThisMonth;

    /**
     * 更新日期
     */
    @TableField("last_update_date")
    private LocalDate lastUpdateDate;

    /**
     * 是否结清 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("finish")
    private Integer finish;

    /**
     * 是否同业融资 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField("is_same_biz")
    private Integer isSameBiz;

    public void generateQueryKey() {
        this.queryKey = this.financingId + "@" + this.financingType;
    }
}
