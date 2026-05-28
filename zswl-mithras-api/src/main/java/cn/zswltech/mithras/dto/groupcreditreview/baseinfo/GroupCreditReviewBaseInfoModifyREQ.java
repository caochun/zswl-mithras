package cn.zswltech.mithras.dto.groupcreditreview.baseinfo;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审基本信息表编辑-请求体")
public class GroupCreditReviewBaseInfoModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    @NotNull
    private Long id;

    /**
    * 授信说明
    */
    @ApiModelProperty(value = "授信说明")
    private String projBackground;


    @ApiModelProperty(value = "项目类型")
//    @NotNull
    @Deprecated
    private String projectType;

    /**
    * 申报授信金额
    */
    @ApiModelProperty(value = "申报授信金额")
    @NotNull
    private Long applyCreditAmount;

    /**
    * 额度有效期限月数
    */
    @ApiModelProperty(value = "额度有效期限月数")
    @NotNull
    private Integer validMonthCount;

    /**
    * 额度是否可循环
    */
    @ApiModelProperty(value = "额度是否可循环")
    @NotNull
    private Integer creditAmountLoop;

    /**
    * 项目主办用户id
    */
    @ApiModelProperty(value = "项目主办用户id")
    @NotNull
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
    @NotNull
    private Long bizDeptId;

    /**
    * 业务部门负责人id
    */
    @ApiModelProperty(value = "业务部门负责人id")
    @NotNull
    private Long bizDeptLeaderId;

    /**
    * 业务分管领导id
    */
    @ApiModelProperty(value = "业务分管领导id")
    @NotNull
    private Long bizDivisionLeaderId;

    /**
    * 风控经理id
    */
    @ApiModelProperty(value = "风控经理id")
    @NotNull
    private Long riskControlManagerId;

    /**
    * 法务经理id
    */
    @ApiModelProperty(value = "法务经理id")
    @NotNull
    private Long legalManagerUserId;

}
