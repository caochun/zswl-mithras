package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @author yangxiong
 * @description 台账界面分页请求参数实体
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeaseLedgerMainREQ extends PageReq {

    @ApiModelProperty(value = "记录ID集合")
    private List<Long> ids;

    @ApiModelProperty(value = "租赁物审核流程编号")
    private String leaseAuditFlowNumber;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "租赁物状态")
    private String leaseStatus;

    @ApiModelProperty(value = "客户id")
    private String clientId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目主办")
    private Long projectOrganizerId;

    @ApiModelProperty(value = "项目协办")
    private Long projectCoOrganizerIds;
}
