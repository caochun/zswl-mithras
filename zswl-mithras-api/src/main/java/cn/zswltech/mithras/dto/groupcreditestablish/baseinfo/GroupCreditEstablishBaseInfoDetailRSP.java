package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信立项基本信息详情-返回体")
public class GroupCreditEstablishBaseInfoDetailRSP extends ListBaseRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 授信主体客户id
    */
    @ApiModelProperty(value = "授信主体客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
    * 授信主体存量风险敞口
    */
    @ApiModelProperty(value = "授信主体存量风险敞口")
    private Long clientRiskExposure;

    /**
    * 授信名称
    */
    @ApiModelProperty(value = "授信名称")
    private String projName;

    /**
    * 项目编号
    */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
    * 授信说明
    */
    @ApiModelProperty(value = "授信说明")
    private String projBackground;

    /**
    * 申报授信金额
    */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
    * 额度有效期限月数
    */
    @ApiModelProperty(value = "额度有效期限月数")
    private Integer validMonthCount;

    /**
    * 额度是否可循环
    */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

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

    @ApiModelProperty(value = "项目协办方用户名称列表")
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

    @ApiModelProperty(value = "业务部门负责人名称")
    private String bizDeptLeaderName;

    /**
    * 业务分管领导id
    */
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    @ApiModelProperty(value = "业务分管领导名称")
    private String bizDivisionLeaderName;

    /**
     * 风控经理id
     */
    @ApiModelProperty(value = "风控经理id")
    private List<Long> riskControlManagerId;

    @ApiModelProperty(value = "风控经理名称")
    private List<String> riskControlManagerName;

    /**
    * 立项状态
    */
    @ApiModelProperty(value = "立项状态")
    private String groupCreditEstablishStatus;

    /**
    * 流程状态
    */
    @ApiModelProperty(value = "流程状态")
    private String groupCreditEstablishProcessStatus;

    @ApiModelProperty("是否业务部门")
    private Boolean isBizDept;

    @ApiModelProperty(value = "客户评级id")
    private Long clientRatingScoreId;

    @ApiModelProperty("客户评级")
    private String clientRatingScore;
}
