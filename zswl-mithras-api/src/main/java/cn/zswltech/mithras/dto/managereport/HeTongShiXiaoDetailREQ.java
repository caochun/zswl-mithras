package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/1/5
 * @description
 */
@Data
public class HeTongShiXiaoDetailREQ {
    @NotNull(message = "<查询日期-起>不能为空")
    @ApiModelProperty("查询日期-起，格式yyyy-MM")
    private LocalDate queryDateFrom;

    @NotNull(message = "<查询日期-止>不能为空")
    @ApiModelProperty("查询日期-止，格式yyyy-MM")
    private LocalDate queryDateTo;

    @ApiModelProperty("部门ID")
    private Long bizDeptId;

    @ApiModelProperty("业务类型")
    private String businessCategory;

    @ApiModelProperty("租赁类型")
    private String leaseType;
}
