package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 租后调整信息表
 * @author jackerhe
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表列表-返回体")
public class AfterLeaseAdjustDetailRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "项目id")
    private Long projId;

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

    @ApiModelProperty(value = "项目协办方用户名列表")
    private List<String> projCosponsorUserNames;

    /**
    * 业务部门id
    */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    /**
    * 业务部门负责人id
    */
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;

    /**
     * 业务部门负责人名称
     */
    @ApiModelProperty(value = "业务部门负责人名称")
    private String bizDeptLeaderName;

    /**
    * 业务分管领导id
    */
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    /**
     * 业务分管领导名称
     */
    @ApiModelProperty(value = "业务分管领导名称")
    private String bizDivisionLeaderName;

    /**
    * 申报授信金额
    */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
    * 业务调整类型，展期，调整还款类型
    */
    @ApiModelProperty(value = "业务调整类型，展期，调整还款类型")
    private String afterLeaseAdjustType;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
    * 展期月数
    */
    @ApiModelProperty(value = "展期月数")
    private Long extensionmonth;

    /**
     * 租赁期限月数
     */
    @ApiModelProperty(value = "租赁期限月数")
    private Integer leaseMonthCount;

    /**
     * 调整说明
     */
    @ApiModelProperty(value =  "adjust_explain")
    private String adjustExplain;

    /**
    * 风控经理id
    */
    @ApiModelProperty(value = "风控经理id")
    private Long riskControlManagerId;

    /**
     * 风控经理Name
     */
    @ApiModelProperty(value = "风控经理Name")
    private String riskControlManagerName;

    /**
    * 法务经理id
    */
    @ApiModelProperty(value = "法务经理id")
    private Long legalManagerUserId;

    /**
     * 法务经理Name
     */
    @ApiModelProperty(value = "法务经理Name")
    private String legalManagerName;

    @ApiModelProperty(value = "调整流程状态 ProjProcessState")
    private String adjustProcessStatus;

}
