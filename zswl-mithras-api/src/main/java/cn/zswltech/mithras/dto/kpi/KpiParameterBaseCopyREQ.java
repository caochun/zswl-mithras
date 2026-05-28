package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
@ApiModel("绩效考核-参数设置基本表删除-请求体")
public class KpiParameterBaseCopyREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(value = "生效月份")
    private LocalDate effectMonth;

}
