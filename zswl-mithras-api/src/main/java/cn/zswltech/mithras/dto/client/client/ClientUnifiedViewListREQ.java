package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class ClientUnifiedViewListREQ extends PageReq {

    @ApiModelProperty("部门编号")
    private List<Long> orgCodeList;

    @ApiModelProperty("客户状态")
    private List<String> clientStatus;

    private String clientName;

}
