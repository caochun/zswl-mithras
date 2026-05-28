package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
@Data
@ApiModel("租后检查项目-项目（更改为客户）信息-返回体")
public class AfterLeaseCheckClientInfoRSP {
    @ApiModelProperty("检查计划的项目（更改为客户）记录id")
    private Long id;

    @ApiModelProperty("检查计划id")
    private Long planId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @Deprecated
    @ApiModelProperty("客户归属主办")
    private Long clientBelongSponsorId;

//    @ApiModelProperty("项目评审的项目id")
//    private Long projectId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

//    @ApiModelProperty("项目主办id")
//    private Long projectSponsorId;

    @ApiModelProperty("报告类型")
    private String reportType;

    @ApiModelProperty("报告模板版本")
    private String reportTemplateType;

    @ApiModelProperty("检查方式")
    private String checkWay;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("承租人/债权人列表")
    private List<ClientInfo> lesseeList;

    @ApiModelProperty("担保人列表")
    private List<ClientInfo> guarantorList;

    @ApiModelProperty(value = "当前处理人")
    private String curAssigneeIds;

    @ApiModelProperty("检查类型")
    private String planType;
}
