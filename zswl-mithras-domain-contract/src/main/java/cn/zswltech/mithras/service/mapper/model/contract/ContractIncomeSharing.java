package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/4/9
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_income_sharing")
public class ContractIncomeSharing extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("contract_id")
    private Long contractId;

    @TableField("receipt_id")
    private Long receiptId;

    @TableField("batch_sequence")
    private String batchSequence;

    @TableField("income_date")
    private LocalDate incomeDate;

    @TableField("income_phase")
    private Integer incomePhase;

    @TableField("begin_of_term_balance")
    private Long beginOfTermBalance;

    @TableField("rent")
    private Long rent;

    @TableField("income")
    private Long income;

    @TableField("tax")
    private Long tax;

    @TableField("income_without_tax")
    private Long incomeWithoutTax;

    @TableField("end_of_term_balance")
    private Long endOfTermBalance;

    @TableField("daily_discount_rate")
    private String dailyDiscountRate;

    @TableField("is_confirmed")
    private Integer isConfirmed;

    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    @TableField("confirm_batch")
    private String confirmBatch;
}
