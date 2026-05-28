package cn.zswltech.mithras.dto.payment.lib;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.policy.PolicyInfoMaterialsListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info列表-返回体")
public class PaymentPolicyInfoListRSP extends ListBaseRSP{

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 保单编号
    */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    /**
    * 保单金额
    */
    @ApiModelProperty(value = "保单金额")
    private Long policyAmount;

    /**
    * 保险起始日
    */
    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDate;

    /**
    * 保险到期日
    */
    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDate;

    /**
    * 保险公司名称
    */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    @ApiModelProperty("保险种类")
    private String policyType;


    @ApiModelProperty(value = "renew_insurance_flag")
    private String renewInsuranceFlag;

    /**
     * 备注
     \     */
    @ApiModelProperty(value = "remark")
    private String remark;

    @ApiModelProperty(value = "create_time")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "create_by")
    private Long createBy;
    @ApiModelProperty(value = "createName")
    private String createName;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    @ApiModelProperty(value = "合同到期日")
    private LocalDate actualFinishDate;

    @ApiModelProperty("附件列表")
    private List<PolicyInfoMaterialsListRSP> files;
}
