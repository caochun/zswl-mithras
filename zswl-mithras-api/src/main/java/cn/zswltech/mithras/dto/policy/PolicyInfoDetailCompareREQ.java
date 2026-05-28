package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("保单信息对比-请求体")
public class PolicyInfoDetailCompareREQ extends VersionBaseREQ {

    @NotNull
    @ApiModelProperty(value = "保单id", required = true)
    private Long id;

}
