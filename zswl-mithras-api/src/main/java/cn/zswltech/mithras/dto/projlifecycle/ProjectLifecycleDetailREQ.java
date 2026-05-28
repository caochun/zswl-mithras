package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-10-23
 **/

@Data
public class ProjectLifecycleDetailREQ {
    @ApiModelProperty("立项id")
    private Long establishId;

    @ApiModelProperty("评审id")
    private Long reviewId;
}
