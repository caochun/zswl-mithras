package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 租后检查计划-基本信息表
 * @author vico
 * @date 2024-04-22
 */
@Data
@ApiModel("租后检查计划-基本信息表列表-返回体")
public class AfterLeaseCheckChangeRecordListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 计划id
    */
    @ApiModelProperty(value = "计划id")
    private String planId;

    /**
    * 计划-客户id
    */
    @ApiModelProperty(value = "计划-客户id")
    private String checkPlanClientId;

    /**
    * 变更前内容
    */
    @ApiModelProperty(value = "变更前内容")
    private String oldContent;

    /**
    * 变更后内容
    */
    @ApiModelProperty(value = "变更后内容")
    private String nowContent;

    /**
    * 审批状态
    */
    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    private LocalDateTime createTime;

    private Long createBy;

    private String createByName;

}
