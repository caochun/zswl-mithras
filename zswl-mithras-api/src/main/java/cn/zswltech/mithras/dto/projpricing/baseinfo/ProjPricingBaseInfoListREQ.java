package cn.zswltech.mithras.dto.projpricing.baseinfo;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 项目评审基本信息高级查询-请求体
 * @author zhaozhengkang
 * @date 2022-08-02
 */
@Data
@ApiModel("项目评审基本信息高级查询-请求体")
public class ProjPricingBaseInfoListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("项目协办")
    private Long projCosponsorUserId;

    @ApiModelProperty("申报授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty("定价状态")
    private String projPricingStatus;

    @ApiModelProperty("定价审批状态")
    private String projPricingProcessStatus;

    @ApiModelProperty("创建时间从")
    private LocalDate createFrom;

    @ApiModelProperty("创建时间到")
    private LocalDate createTo;

    @ApiModelProperty("更新时间从")
    private LocalDate updateFrom;

    @ApiModelProperty("更新时间到")
    private LocalDate updateTo;

}
