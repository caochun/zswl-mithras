package cn.zswltech.mithras.payment.model;

import cn.zswltech.mithras.policy.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.enums.PolicyTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
public class PaymentPolicyInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("contract_id")
    private Long contractId;

    @TableField("payment_id")
    @IncludeNull
    private Long paymentId;
    /**
    * 保单编号
    */
    @TableField("policy_code")
    private String policyCode;

    /**
    * 保单金额
    */
    @TableField("policy_amount")
    private Long policyAmount;

    /**
     * 保单种类
     * {@link PolicyTypeEnum#name()}
     */
    @TableField("policy_type")
    @IncludeNull
    private String policyType;

    /**
    * 保险起始日
    */
    @TableField("insurance_start_date")
    private LocalDate insuranceStartDate;

    /**
    * 保险到期日
    */
    @TableField("insurance_end_date")
    private LocalDate insuranceEndDate;

    /**
    * 保险公司名称
    */
    @TableField("insurance_company")
    private String insuranceCompany;

    /**
     * 是否续保
     * {@link PolicyRenewInsuranceEnum#name()}
     */
    @TableField("renew_insurance_flag")
    @IncludeNull
    private String renewInsuranceFlag;

    /**
     * 备注
     \     */
    @TableField("remark")
    @IncludeNull
    private String remark;

    /**
     * 标识信息
     **/
    @TableField("identification_information")
    private String identificationInformation;

    @Override
    public void setMainId(Long id) {
        setPaymentId(id);
    }

    @Override
    public Long getMainId() {
        return getPaymentId();
    }
}
