package cn.zswltech.mithras.dto.client.external;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 列表查询 外部信息
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:20 PM
 */
@Data
@ApiModel("外部信息-列表查询")
public class ExternalPageREQ extends PageReq {

    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

}
