package cn.zswltech.mithras.service.mapper.model.fund.receiptrepay;

import cn.zswltech.mithras.service.enums.capital.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/3
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_receipt_flow_detail")
public class FundReceiptFlowDetail extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "data_source")
    private String dataSource;

    @TableField(value = "receipt_repay_id")
    private Long receiptRepayId;

    @TableField(value = "settle_method")
    private String settleMethod;

    @TableField(value = "cash_flow_code")
    private String cashFlowCode;

    /**
     * {@link FinanceCashFlowItemEnum#name()}
     **/
    @TableField(value = "cash_flow_item")
    private String cashFlowItem;

    @TableField(value = "cash_flow_date")
    private LocalDate cashFlowDate;

    @TableField(value = "total_amount")
    private Long totalAmount;

    @TableField(value = "principal_amount")
    private Long principalAmount;

    @TableField(value = "interest_amount")
    private Long interestAmount;

    @TableField(value = "bank_flow_no")
    private String bankFlowNo;

    @TableField(value = "finance_flow_id")
    private Long financeFlowId;

    @TableField(value = "our_account_number")
    private String ourAccountNumber;

    @TableField(value = "our_account_name")
    private String ourAccountName;

    @TableField(value = "our_account_bank")
    private String ourAccountBank;
}
