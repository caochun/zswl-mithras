package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 保单暂存表c
 * @author vico
 * @date 2023-10-23
 */
@Data
@ApiModel("保单暂存表c列表-返回体")
public class PolicyInfoTmpListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

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
    * 通知标识 0未通知， 1，已通知
    */
    @ApiModelProperty(value = "通知标识 0未通知， 1，已通知")
    private Integer noticeFlag;

    /**
    * 续保结果 0需要续保，1 已续保或不需续保
    */
    @ApiModelProperty(value = "续保结果 0需要续保，1 已续保或不需续保")
    private Long renewInsuranceResult;

    /**
    * 保险公司名称
    */
    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    /**
    * contract_code
    */
    @ApiModelProperty(value = "contract_code")
    private String contractCode;

    /**
    * 保单状态
    */
    @ApiModelProperty(value = "保单状态")
    private String policyStatus;

    /**
    * 标识信息
    */
    @ApiModelProperty(value = "标识信息")
    private String identificationInformation;

    /**
    * 是否自动推送，0 手动 1 自动
    */
    @ApiModelProperty(value = "是否自动推送，0 手动 1 自动")
    private Integer automatic;

    /**
    * 保单种类
    */
    @ApiModelProperty(value = "保单种类")
    private String policyType;

    /**
    * 是否续保
    */
    @ApiModelProperty(value = "是否续保")
    private String renewInsuranceFlag;

    /**
     * 上级保单id
     */
    @ApiModelProperty(value = "上级保单id")
    private Long parentPolicyId;

    /**
     * 是否为暂存数据
     */
    @ApiModelProperty(value = "是否为暂存数据")
    private Boolean isTemp = false;

    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("附件列表")
    private List<PolicyInfoMaterialsListRSP> files;

    private LocalDateTime createTime;

    private Long createBy;

    private String createName;

}
