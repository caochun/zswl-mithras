package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @date 2023/6/16
 * @description 科目余额表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_bcm_balance_mf")
public class FinanceBcmBalanceMf extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    /**
     * 组织编码
     */
    @TableField(value = "org_code")
    private String org_code;

    /**
     * 组织名称
     */
    @TableField(value = "org_name")
    private String org_name;

    /**
     * 财年
     */
    @TableField(value = "f_year")
    private Integer f_year;
    /**
     * 期间
     */
    @TableField(value = "f_period")
    private Integer f_period;
    /**
     * 期间-年月
     */
    @TableField(value = "year_period")
    private String year_period;

    /**
     * 科目编码
     */
    @TableField(value = "acct_no")
    private String acct_no;
    /**
     * 科目名称
     */
    @TableField(value = "acct_name")
    private String acct_name;
    /**
     * 辅助核算id
     */
    @TableField(value = "fassgrpid")
    private String fassgrpid;

    /**
     * 科目编码转译@本年累计/上年同期@贷方金额/借方金额
     **/
    @TableField(value = "risk_name")
    private String riskName;

    /**
     * 金额
     **/
    @TableField(value = "risk_value")
    private BigDecimal riskValue;

    /**
     * 插入时间
     */
    @TableField(value = "insert_time")
    private String insert_time;


}
