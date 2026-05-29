package cn.zswltech.mithras.service.mapper.model.fund.financing;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_pledge_info")
public class FundFinancingPledgeInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;
    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;
    /**
     * 质押编号
     */
    @TableField("pledge_code")
    private String pledgeCode;
    /**
     * 业务部门id
     */
    @TableField("biz_dept_id")
    private Long bizDeptId;
    /**
     * 项目评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;
    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;
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
     * 业务类型
     */
    @TableField("biz_type")
    private String bizType;
    /**
     * 合同金额
     */
    @Deprecated
    @TableField("contract_amount")
    private Long contractAmount;
    /**
     * 合同开始日期
     */
    @Deprecated
    @TableField("contract_start_date")
    private LocalDate contractStartDate;
    /**
     * 合同结束日期
     */
    @Deprecated
    @TableField("contract_end_date")
    private LocalDate contractEndDate;

    /**
     * 剩余未还本金
     */
    @Deprecated
    @TableField("remaining_unpaid_principal")
    private Long remainingUnpaidPrincipal;

    /**
     * 账户名称
     */
    @TableField(value = "account_name",fill = FieldFill.UPDATE)
    private String accountName;

    /**
     * 银行账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 开户银行
     */
    @TableField("account_bank")
    private String accountBank;

    /**
     * 是否质押
     */
    @TableField("is_pledge")
    private Boolean isPledge;

    /**
     * 是否监管
     */
    @TableField("is_supervise")
    private Boolean isSupervise;

    /**
     * 锁定合同标记
     */
    @TableField("lock_contract")
    private String lockContract;

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}