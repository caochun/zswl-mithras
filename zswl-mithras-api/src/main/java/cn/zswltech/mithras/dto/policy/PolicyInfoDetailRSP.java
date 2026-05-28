package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel("保单详情-返回体")
public class PolicyInfoDetailRSP extends ListBaseRSP {

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

    @ApiModelProperty("contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @ApiModelProperty("contract_code")
    private String contractCode;

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

    @ApiModelProperty(value = "保单金额")
    private Long policyAmount;

    /**
     * 保险公司名称
     */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

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

    private Boolean creater;

    private String approvalStatus;

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


    @ApiModelProperty("创建人ID")
    private Long createBy;

    /**
     * 合同编号
     */
    @ApiModelProperty("创建人姓名")
    private String createName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 标识信息
     */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    @ApiModelProperty(value = "保单等级")
    private Integer level;

    private Long parentId;

    private List<PolicyInfoDetailRSP> children;
}
