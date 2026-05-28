package cn.zswltech.mithras.dto.contract.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author vico
 * @description 合同基本信息表
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息表编辑-请求体")
public class ContractBaseInfoModifyREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @NotNull(message = "项目id不能为空")
    private Long id;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    /**
     * 剩余可用额度(元)
     */
    @ApiModelProperty(value = "剩余可用额度(元)")
    private Long remainAvailableQuota;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String leaseType;

    /**
     * 保理类型
     */
    @ApiModelProperty(value = "保理类型")
    private String factoringType;

    /**
     * 债权转让类型
     */
    @ApiModelProperty(value = "转让类型")
    private String zrType;

    /**
     * 转租赁业务专用字段
     */
    @ApiModelProperty(value = "转让方")
    private String assignor;

    /**
     * 项目类型：公共事业类、省内国（央）企、其他
     */
    @ApiModelProperty(value = "项目类型：公共事业类、省内国（央）企、其他")
    private String projectType;

    /**
     * 风险等级
     */
    @ApiModelProperty(value = "风险等级")
    private String riskLevel;

    /**
     * 项目来源：存量翻单、渠道介绍、自主开发
     */
    @ApiModelProperty(value = "项目来源：存量翻单、渠道介绍、自主开发")
    private String projSource;

    /**
     * 资金用途
     */
    @ApiModelProperty(value = "资金用途")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @ApiModelProperty(value = "项目背景")
    private String projBackground;

    /**
     * 项目主办用户id
     */
    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    /**
     * 项目协办方用户id列表
     */
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;

    /**
     * 业务部门id
     */
    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;

    /**
     * 业务分管领导id
     */
    @ApiModelProperty(value = "业务分管领导id")
    private Long bizDivisionLeaderId;

    /**
     * 关联的评审id
     */
    @ApiModelProperty(value = "关联的评审id")
    private Long projReviewId;

    @ApiModelProperty(value = "支付申请次数")
    private Long paymentCount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "租赁物类型 LeaseItemTypeEnum")
    private List<String> leaseItemTypes;

}
