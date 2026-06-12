package cn.zswltech.mithras.policy.persistence.model;

import cn.zswltech.mithras.policy.enums.PolicyRenewInsuranceEnum;
import cn.zswltech.mithras.policy.enums.PolicyStatusEnum;
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
 * @author wwj
 * @description 保单信息表
 * @date 2023-06-15
 */
@Data
public class PolicyInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("proj_id")
    private Long projId;

    @TableField("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    @TableField("payment_id")
    private Long paymentId;

    //付款保单id，付款一定是起始
    @TableField("payment_policy_id")
    private Long paymentPolicyId;

    /**
    * 保单编号
    */
    @TableField("policy_code")
    @IncludeNull
    private String policyCode;

    /**
    * 保单金额
    */
    @TableField("policy_amount")
    @IncludeNull
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
    @IncludeNull
    private LocalDate insuranceStartDate;

    /**
    * 保险到期日
    */
    @TableField("insurance_end_date")
    @IncludeNull
    private LocalDate insuranceEndDate;

    /**
    * 保险公司名称
    */
    @TableField("insurance_company")
    @IncludeNull
    private String insuranceCompany;

    /**
     * 是否续保
     * {@link PolicyRenewInsuranceEnum#name()}
     */
    @TableField("renew_insurance_flag")
    @IncludeNull
    private String renewInsuranceFlag;

    /**
     * 续保结果
     * 0需要续保，1 已续保或不需续保
     */
    @TableField("renew_insurance_result")
    @IncludeNull
    private Integer renewInsuranceResult;

    /**
     * 备注
     */
    @TableField("remark")
    @IncludeNull
    private String remark;

    /**
     * 审批状态
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     *{@link PolicyStatusEnum#name()}
     **/
    @TableField("policy_status")
    private String policyStatus;

    /**
     * 是否自动推送，0 手动 1 自动
     */
    @TableField("automatic")
    private Integer automatic;

    @TableField("parent_id")
    private Long parentId;

    @TableField("level")
    private Integer level;

    /**
     *通知标识 0未通知， 1，已通知
     **/
    @TableField("notice_flag")
    private Integer noticeFlag;

    /**
     * 保单到期标识 0， 1，已通知
     **/
    @TableField("expiration_reminder_flag")
    private Integer expirationReminderFlag;

    /**
     * 续保逾期标识 0未通知， 1，已通知
     **/
    @TableField("renewal_overdue_flag")
    private Integer renewalOverdueFlag;

    /**
     * 标识信息
     **/
    @TableField("identification_information")
    @IncludeNull
    private String identificationInformation;

    /**
     * 数据状态
     **/
    @TableField("data_status")
    private String dataStatus;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
