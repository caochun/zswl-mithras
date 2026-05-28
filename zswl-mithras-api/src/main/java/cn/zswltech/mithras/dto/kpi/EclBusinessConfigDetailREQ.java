package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description ecl_业务配置表
 * @author vico
 * @date 2025-09-24
 */
@Data
@ApiModel("ecl_业务配置表详情-请求体")
public class EclBusinessConfigDetailREQ {

    //@NotNull(message = "详情id不能为空")
    private Long id;

    /**
     * 配置code
     */
    @ApiModelProperty(value = "配置code EclConfigEnum")
    private String configCode;
}
