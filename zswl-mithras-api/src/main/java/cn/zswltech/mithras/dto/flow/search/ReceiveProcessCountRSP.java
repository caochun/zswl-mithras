package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 我收到的
 *
 * @author wangchuanhao
 * @date 2022/8/8 5:36 PM
 */
@Data
public class ReceiveProcessCountRSP {

    @ApiModelProperty("待办数量")
    private Long todoCount;

    @ApiModelProperty("已办数量")
    private Long doneCount;

    @ApiModelProperty("抄送我的数量")
    private Long ccCount;

}
