package cn.zswltech.mithras.dto.dashboard.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/15/16:48
 * @description
 */
@Data
public class DistributionClientDepartmentListRSP {
    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "客户数")
    private String value;

    @ApiModelProperty(value = "客户数单位")
    private String unit;
}
