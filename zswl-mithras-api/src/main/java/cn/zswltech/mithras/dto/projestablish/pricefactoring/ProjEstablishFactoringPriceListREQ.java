package cn.zswltech.mithras.dto.projestablish.pricefactoring;
import cn.zswltech.mithras.dto.PageReq;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 保理报价方案表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("保理报价方案表列表-请求体")
public class ProjEstablishFactoringPriceListREQ extends PageReq {
    @ApiModelProperty("所属立项Id")
    private Long projEstablishId;
}
