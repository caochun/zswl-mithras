package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardProjectProvisionREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("部门ID")
    private Long bizDeptId;
    private List<Long> ids;
}
