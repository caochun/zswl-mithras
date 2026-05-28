package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ClientListREQ extends PageReq {

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("行业类型")
    private String industryType;

    @ApiModelProperty("创建日期-从")
    private LocalDate createDateFrom;

    @ApiModelProperty("创建日期-到")
    private LocalDate createDateTo;

    @ApiModelProperty("更新日期-从")
    private LocalDate updateDateFrom;

    @ApiModelProperty("更新日期-到")
    private LocalDate updateDateTo;

    @ApiModelProperty("客户状态")
    private String clientStatus;

    @ApiModelProperty("流程状态")
    private String processStatus;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建部门")
    private Long createByDeptId;

    @ApiModelProperty("只选择已生效客户")
    private Boolean effected;

    @ApiModelProperty("是否需要展示是否需要审批的状态（流程状态额外信息展示）")
    private Boolean showApprovalFlag;

    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

    @ApiModelProperty("所属负责项目经理的id")
    private Long belongSponsorId;

    @ApiModelProperty("是否集团公司 0-否 1-是")
    private Boolean isGroup;

    @ApiModelProperty("普通查询-query，选做担保人/抵押人/质押人-other，选做承租人/债权人/债务人-main")
    private String scene;

    @ApiModelProperty("是否包含航运模型客户")
    private Boolean containHymx;
}
