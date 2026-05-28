package cn.zswltech.mithras.dto.client.infohistory;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@Data
public class InfoHistoryDetailRSP {
    @ApiModelProperty("变更前数据")
    private String originalData;

    @ApiModelProperty("变更后数据")
    private String currentData;

}
