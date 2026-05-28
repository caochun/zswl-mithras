package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel("保单台账-保单详情-返回体")
public class PolicyLedgerRenewInsuranceRSP {

    @ApiModelProperty("id")
    private Long id;


    /**
     * 保单编号
     */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险机构")
    private String insuranceCompany;

    /**
     * 保单种类
     * PolicyTypeEnum
     */
    @ApiModelProperty("保险种类")
    private String policyType;


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
     * 备注
     */
    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    private Long createBy;

    private String createByName;

    private LocalDateTime createTime;

    @ApiModelProperty("资料清单")
    private List<PolicyInfoMaterialsListRSP> materials;


}
