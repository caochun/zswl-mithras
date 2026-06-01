package cn.zswltech.mithras.service.mapper.model.policy;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Data
public class PolicyInfoTmp extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

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
    * 通知标识 0未通知， 1，已通知
    */
    @TableField("notice_flag")
    private Integer noticeFlag;

    /**
    * 续保结果 0需要续保，1 已续保或不需续保
    */
    @TableField("renew_insurance_result")
    private Long renewInsuranceResult;

    /**
    * 保险公司名称
    */
    @TableField("insurance_company")
    private String insuranceCompany;

    /**
    * contract_code
    */
    @TableField("contract_code")
    private String contractCode;

    /**
    * 保单状态
    */
    @TableField("policy_status")
    private String policyStatus;

    /**
    * 标识信息
    */
    @TableField("identification_information")
    private String identificationInformation;

    /**
    * 是否自动推送，0 手动 1 自动
    */
    @TableField("automatic")
    private Integer automatic;

    /**
    * 保单种类
    */
    @TableField("policy_type")
    private String policyType;

    /**
    * 是否续保
    */
    @TableField("renew_insurance_flag")
    private String renewInsuranceFlag;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

}
