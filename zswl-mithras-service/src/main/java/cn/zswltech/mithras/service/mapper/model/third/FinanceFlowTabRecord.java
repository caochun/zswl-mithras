package cn.zswltech.mithras.service.mapper.model.third;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * tab中流水详情记录
 *
 * @author bigbear
 * @TableName finance_flow_tab_record
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "finance_flow_tab_record")
public class FinanceFlowTabRecord extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联tab主表ID
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 银行流水类型，收/付
     */
    @TableField(value = "finance_flow_type")
    private String financeFlowType;

    /**
     * 银行流水ID
     */
    @TableField(value = "finance_flow_id")
    private Long financeFlowId;

    /**
     * 银行流水编号
     */
    @TableField(value = "bank_flow_no")
    private String bankFlowNo;

    /**
     * 对方户名
     */
    @TableField(value = "other_account_name")
    private String otherAccountName;

    /**
     * 对方账号
     */
    @TableField(value = "other_account_number")
    private String otherAccountNumber;

    /**
     * 交易日期
     */
    @TableField(value = "biz_date")
    private LocalDate bizDate;

    /**
     * 交易金额，存毫厘
     */
    @TableField(value = "biz_amount")
    private Long bizAmount;

    /**
     * 剩余可核销金额，存毫厘
     */
    @TableField(value = "surplus_amount")
    private Long surplusAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}