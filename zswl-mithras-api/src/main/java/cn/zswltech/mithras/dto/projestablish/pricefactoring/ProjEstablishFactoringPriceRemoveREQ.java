package cn.zswltech.mithras.dto.projestablish.pricefactoring;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 保理报价方案表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("保理报价方案表删除-请求体")
public class ProjEstablishFactoringPriceRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
