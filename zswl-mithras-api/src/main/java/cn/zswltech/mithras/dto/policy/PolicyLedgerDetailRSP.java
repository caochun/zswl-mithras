package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@ApiModel("保单台账-保单详情-返回体")
public class PolicyLedgerDetailRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("付款id")
    private Long paymentId;
    /**
     * 保单编号
     */
    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    @ApiModelProperty(value = "projId")
    private Long projId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名")
    private String clientName;

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

    @ApiModelProperty(value = "保单金额")
    private Long policyAmount;

    /**
     * 保单种类
     * PolicyTypeEnum
     */
    @ApiModelProperty("保险种类")
    private String policyType;

    /**
     * 备注
     */
    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    /**
     * 逾期天数
     **/
    private Integer overdueDays;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;
    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户名列表")
    private List<String> projCosponsorUserNames;

    @ApiModelProperty(value = "合同到期日")
    private LocalDate actualFinishDate;

    @ApiModelProperty("资料清单")
    private List<PolicyInfoMaterialsListRSP> materials;
}
