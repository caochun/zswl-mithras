package cn.zswltech.mithras.service.mapper.model.process.prepare;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author luyi
 */
@Data
@TableName("financing_repay_actual_process_detail")
public class FinancingRepayActualProcessDetail extends BaseModelWithLogicDelete implements Serializable, IEntity {
    private static final long serialVersionUID = -9025392687670931311L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

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
     * 融资机构
     */
    @TableField(value = "organization_name")
    private String organizationName;

    /**
     * 融资类型：直融/间融
     */
    @TableField("financing_type")
    private String financingType;

    /**
     * 还款实际表id
     */
    @TableField("financing_repay_actual_id")
    private Long financingRepayActualId;

    /**
     * 融资金额
     */
    @TableField("financing_amount")
    private Long financingAmount;

    /**
     * 应还日期
     */
    @TableField("repay_date")
    private LocalDate repayDate;

    /**
     * 应还总额
     */
    @TableField("repay_amount")
    private Long repayAmount;

    /**
     * 应还本金
     */
    @TableField("principle_amount")
    private Long principleAmount;

    /**
     * 应还利息
     */
    @TableField("interest_amount")
    private Long interestAmount;

    /**
     * 账号
     */
    @TableField("account_number")
    private String accountNumber;

    /**
     * 支行名称
     */
    @TableField("account_bank")
    private String accountBank;


    /**
     * 账户类别
     */
    @TableField("fund_financing_account_type")
    private String fundFinancingAccountType;


    /**
     * 预备表id
     */
    @TableField("prepare_id")
    private Long prepareId;

    /**
     * 确认状态是否已确认
     */
    @TableField(value = "is_confirmed")
    private Integer isConfirmed;

    /**
     * 还款状态是否已确认
     */
    @TableField(value = "is_paid")
    private Integer isPaid;



    @Override
    public void setMainId(Long id) {
        this.prepareId = id;
    }

    @Override
    public Long getMainId() {
        return this.prepareId;
    }
}
