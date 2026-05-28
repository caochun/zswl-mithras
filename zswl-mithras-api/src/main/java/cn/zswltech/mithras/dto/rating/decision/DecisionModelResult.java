package cn.zswltech.mithras.dto.rating.decision;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DecisionModelResult {


    @ApiModelProperty(value = "模型名称")
    private String name;

    @ApiModelProperty(value = "模型编号")
    private String code;

}
