package cn.zswltech.mithras.dto.payment.lib;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description payment_policy_info
 * @author zhaozhengkang
 * @date 2022-09-13
 */
@Data
@ApiModel("payment_policy_info编辑-请求体")
public class PaymentPolicyInfoModifyREQ {


    /**
     *
     */
    @ApiModelProperty("payment_id")
    private Long paymentId;

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

    /**
     * 保单种类 PolicyTypeEnum
     *
     */
    @ApiModelProperty(value = "保单种类 PolicyTypeEnum")
    private String policyType;

    /**
     * 是否续保 PolicyRenewInsuranceEnum
     */
    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("删除id")
    private List<Long> removeFileIds;

}
