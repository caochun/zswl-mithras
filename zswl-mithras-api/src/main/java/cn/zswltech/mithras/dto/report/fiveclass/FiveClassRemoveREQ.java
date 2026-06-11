package cn.zswltech.mithras.dto.report.fiveclass;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 征信报送-五级分类表删除入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-五级分类表删除入参")
public class FiveClassRemoveREQ {

    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "businessKey不能为空")
    @ApiModelProperty(value = "业务标识")
    private String businessKey;

    @ApiModelProperty(value = "删除数据原因")
    private String reason;
}
