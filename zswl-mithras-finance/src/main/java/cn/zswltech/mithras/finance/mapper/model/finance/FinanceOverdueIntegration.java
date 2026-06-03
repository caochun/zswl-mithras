package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.finance.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
public class FinanceOverdueIntegration extends BaseModelWithLogicDelete implements Serializable, IEntity {

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
    * 收款编号
    */
    @TableField("collection_code")
    private String collectionCode;

    @TableField("cash_flow_item")
    private String cashFlowItem;

    /**
    * 单据状态
     * {@link OverdueRecordStatueEnum#name()}
    */
    @TableField("record_status")
    private String recordStatus;

    /**
     * 苍穹单据编号
     **/
    @TableField("billno")
    private String billno;

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
    * 国有类型
    */
    @TableField("owned_type")
    private String ownedType;

    /**
    * 实控人
    */
    @TableField("actual_controller")
    private String actualController;

    /**
    * 款项内容.款项内容编码
    */
    @TableField("payment_number")
    private String paymentNumber;

    /**
    * 单据账龄起算日
    */
    @TableField("record_start_date")
    private LocalDate recordStartDate;

    /**
    * 单据日期
    */
    @TableField("record_bill_date")
    private LocalDate recordBillDate;

    /**
    * 科目
    */
    @TableField("accounttype_number")
    private String accounttypeNumber;

    /**
    * 约定收款日期
    */
    @TableField("record_due_date")
    private LocalDate recordDueDate;

    /**
    * 约定收款条件
    */
    @TableField("record_payment_terms")
    private String recordPaymentTerms;

    /**
    * 应收金额（元）
    */
    @TableField("rece_amount")
    private BigDecimal receAmount;

    /**
    * 行业正常收款周期
    */
    @TableField("collection_cycle")
    private Integer collectionCycle;

    /**
    * 审批状态
     * {@link ProjProcessState#name()}
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
