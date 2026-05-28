package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel("保单维护列表-返回体")
public class PolicyInfoListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("付款id")
    private String paymentId;

    //付款保单id
    @ApiModelProperty("付款保单")
    private Long paymentPolicyId;

    @ApiModelProperty(value = "保单id")
    private Long policyId;

    /**
     * 保单编号
     */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    /**
     * 保单种类
     * PolicyTypeEnum
     */
    @ApiModelProperty("保险种类")
    private String policyType;

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

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 备注
     */
    @ApiModelProperty("remark")
    private String remark;


    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人名称")
    private String createName;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    @ApiModelProperty("附件列表")
    private List<PolicyInfoMaterialsListRSP> files;

}
