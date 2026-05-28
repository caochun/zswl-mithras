package cn.zswltech.mithras.dto.afterlease;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表列表-返回体")
public class AfterLeaseAdjustInfoListRSP {
    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 客户id
    */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名")
    private String clientName;

    /**
    * 业务类型。租赁、保理、转租赁
    */
    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
    * 项目名称
    */
    @ApiModelProperty(value = "项目名称")
    private String projName;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目编号")
    private String projCode;
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
    * 申报授信金额
    */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
    * 业务调整类型，展期，调整还款类型
    */
    @ApiModelProperty(value = "业务调整类型，展期，调整还款类型")
    private String afterLeaseAdjustType;

    @ApiModelProperty(value = "业务调整日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private LocalDateTime afterLeaseAdjustData;

    /**
    * 流程状态
    */
    @ApiModelProperty(value = "流程状态 ProcessStatus")
    private String adjustProcessStatus;

}
