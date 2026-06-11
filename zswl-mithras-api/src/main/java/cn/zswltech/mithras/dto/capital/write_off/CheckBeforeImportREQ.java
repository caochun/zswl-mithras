package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/19 18:59
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "导入流水前的校验请求参数")
public class CheckBeforeImportREQ extends WriteOffBaseREQ {

    @ApiModelProperty(value = "前置天数")
    @NotNull(message = "前置天数不能为空")
    private Integer plusDays;

    @ApiModelProperty(value = "选择的流水列表")
    @NotEmpty(message = "选择的流水列表不能为空")
    private List<Long> bankFlowIds;
}
