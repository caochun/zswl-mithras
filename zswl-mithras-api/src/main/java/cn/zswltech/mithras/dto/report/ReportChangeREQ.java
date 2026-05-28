package cn.zswltech.mithras.dto.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 征信报送-报送状态改变入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Data
@ApiModel("征信报送-报送状态改变入参")
public class ReportChangeREQ {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;

    /**
     * {@link cn.zswltech.mithras.report.enums.common.ReportPageEnum}
     */
    @ApiModelProperty(value = "修改模块 ReportPageEnum#name")
    @NotNull(message = "模块不能为空")
    private String module;
}
