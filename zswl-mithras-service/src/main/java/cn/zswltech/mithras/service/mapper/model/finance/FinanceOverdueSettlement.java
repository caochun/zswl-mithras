package cn.zswltech.mithras.service.mapper.model.finance;

import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueSettlementRelationEnum;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author vico
 * @description 应收逾期结算表
 * @date 2025-09-15
 */
@Data
public class FinanceOverdueSettlement extends BaseModelWithLogicDelete implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("overdue_report_id")
    private Long overdueReportId;

    /**
     * 收款明细id
     */
    @TableField("collection_id")
    private Long collectionId;

    /**
     * 核销记录明细id
     **/
    @TableField("collection_record_id")
    private Long collectionRecordId;

    /**
     * 苍穹单据编号
     **/
    @TableField("billno")
    private String billno;

    /**
     * 收款编号
     */
    @TableField("collection_code")
    private String collectionCode;

    @TableField("cash_flow_item")
    private String cashFlowItem;

    /**
     * 单据状态 {@link OverdueRecordStatueEnum#name()}
     */
    @TableField("record_status")
    private String recordStatus;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 单据日期
     */
    @TableField("record_bill_date")
    private LocalDate recordBillDate;

    /**
     * 结算日期
     */
    @TableField("settlement_date")
    private LocalDate settlementDate;

    /**
     * 结算记录的凭证记账日期
     */
    @TableField("voucher_account_date")
    private LocalDate voucherAccountDate;

    /**
     * 结算关系
     * {@link OverdueSettlementRelationEnum#name()}
     */
    @TableField("settlement_relation")
    private String settlementRelation;

    /**
     * 结算金额（元）
     */
    @TableField("settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * 审批状态 ProjProcessState
     */
    @TableField("approval_status")
    private String approvalStatus;

    @Override
    public void setMainId(Long id) {
        this.overdueReportId = id;
    }

    @Override
    public Long getMainId() {
        return overdueReportId;
    }
}
