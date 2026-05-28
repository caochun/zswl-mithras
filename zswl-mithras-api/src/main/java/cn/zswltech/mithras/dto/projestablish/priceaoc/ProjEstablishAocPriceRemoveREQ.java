package cn.zswltech.mithras.dto.projestablish.priceaoc;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 债权转让报价方案表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("债权转让报价方案表删除-请求体")
public class ProjEstablishAocPriceRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
