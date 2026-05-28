package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.PageReq;
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
@ApiModel("绩效考核-参数设置基本表列表-请求体")
public class KpiParameterBaseListREQ extends PageReq {
    @ApiModelProperty(value = "参数状态")
    private String parameterStatus;

    @ApiModelProperty(value = "生效月份")
    private LocalDate effectMonth;
}
