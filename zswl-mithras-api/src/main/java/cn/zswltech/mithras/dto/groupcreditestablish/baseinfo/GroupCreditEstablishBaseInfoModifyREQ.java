package cn.zswltech.mithras.dto.groupcreditestablish.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 集团授信立项基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信立项基本信息表编辑-请求体")
public class GroupCreditEstablishBaseInfoModifyREQ {

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

}
