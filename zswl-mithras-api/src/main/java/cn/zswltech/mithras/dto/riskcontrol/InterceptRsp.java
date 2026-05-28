package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/15 14:17
 */
@Data
@ApiModel("拦截返回体")
public class InterceptRsp {
    @ApiModelProperty("是否拦截,false:拦截,true:不拦截")
    private Boolean intercept;
    @ApiModelProperty("超额指标名称")
    private List<String> metricNames;
}
