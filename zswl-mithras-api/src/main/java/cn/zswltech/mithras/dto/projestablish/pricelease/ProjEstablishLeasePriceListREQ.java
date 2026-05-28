package cn.zswltech.mithras.dto.projestablish.pricelease;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import io.swagger.annotations.ApiModel;
/**
 * @description 租赁报价方案表
 * @author zhaozhengkang
 * @date 2022-07-19
 */
@Data
@ApiModel("租赁报价方案表列表-请求体")
public class ProjEstablishLeasePriceListREQ extends PageReq {
    @ApiModelProperty("所属立项Id")
    private Long projEstablishId;
}
