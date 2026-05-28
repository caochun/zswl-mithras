package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author yangxiong
 * @description 台账分页返回实体
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseLedgerMainRSP {

    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "租赁物审核流程编号")
    private String leaseAuditFlowNumber;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "合同编号")
    private Set<String> contractCode;

    @ApiModelProperty(value = "项目主办")
    private String projectOrganizer;

    @ApiModelProperty(value = "项目协办")
    private List<String> projectCoOrganizer;

    @ApiModelProperty(value = "租赁物状态")
    private String leaseStatus;

    @ApiModelProperty(value = "创建时间, 格式：yyyy-MM-dd HH:mm:ss")
    private String createTime;
}


