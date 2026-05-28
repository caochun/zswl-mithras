package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
@ApiModel("绩效考核-参数设置基本表列表-返回体")
public class KpiParameterConfigCommonReq {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "参数id")
    private Long baseId;

}
