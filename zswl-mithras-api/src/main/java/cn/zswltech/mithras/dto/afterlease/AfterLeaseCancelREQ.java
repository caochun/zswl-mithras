package cn.zswltech.mithras.dto.afterlease;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表列表-请求体")
public class AfterLeaseCancelREQ {

    @NotNull(message = "租后调整主id")
    @ApiModelProperty(value = "租后调整主id")
    private Long mainId;

    private String cancelType;

}
