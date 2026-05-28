package cn.zswltech.mithras.dto.client.clientversion;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 客户版本列表查询
 *
 * @author wangchuanhao
 * @date 2022/6/23 2:35 PM
 */
@Data
@ApiModel("客户版本列表查询-入参")
public class ClientVersionListREQ extends PageReq {

    @ApiModelProperty("客户id")
    @NotNull
    private Long clientId;

}
