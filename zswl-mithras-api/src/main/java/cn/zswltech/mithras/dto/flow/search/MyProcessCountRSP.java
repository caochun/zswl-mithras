package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 我发起的 3个 栏目的数量
 *
 * @author wangchuanhao
 * @date 2022/8/8 5:36 PM
 */
@Data
public class MyProcessCountRSP {

    @ApiModelProperty("申请中数量")
    private Long applyingCount;

    @ApiModelProperty("我的撤回数量")
    private Long withdrawCount;

    @ApiModelProperty("退回我的数量")
    private Long backToStepCount;

    @ApiModelProperty("审批结束数量")
    private Long finishCount;

    @ApiModelProperty("待发起数量")
    private Long pendingCount;

}
