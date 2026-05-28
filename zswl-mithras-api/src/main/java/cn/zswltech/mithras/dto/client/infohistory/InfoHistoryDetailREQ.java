package cn.zswltech.mithras.dto.client.infohistory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class InfoHistoryDetailREQ {
    @NotNull
    @ApiModelProperty("变更历史id")
    private Long id;
}
