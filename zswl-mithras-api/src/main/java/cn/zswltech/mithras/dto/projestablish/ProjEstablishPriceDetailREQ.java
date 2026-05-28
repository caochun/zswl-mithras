package cn.zswltech.mithras.dto.projestablish;

import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("报价方案列表-请求体")
public class ProjEstablishPriceDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("所属立项Id")
    private Long projEstablishId;
}
