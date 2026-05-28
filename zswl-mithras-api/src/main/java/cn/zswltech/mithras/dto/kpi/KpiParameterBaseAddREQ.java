package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
@ApiModel("绩效考核-参数设置基本表新增-请求体")
public class KpiParameterBaseAddREQ {

    /**
    * 生效月份
    */
    @ApiModelProperty(value = "生效月份")
    private LocalDate effectMonth;

    /**
    * 参数状态
    */
    @ApiModelProperty(value = "参数状态")
    private String parameterStatus;

}
