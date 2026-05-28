package cn.zswltech.mithras.dto.client.infohistory;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("变更历史列表-请求体")
public class InfoHistoryListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;
}
