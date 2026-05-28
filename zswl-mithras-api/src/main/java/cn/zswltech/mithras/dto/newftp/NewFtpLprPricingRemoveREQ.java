package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
/**
 * @description LPR定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("LPR定价删除-请求体")
public class NewFtpLprPricingRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
