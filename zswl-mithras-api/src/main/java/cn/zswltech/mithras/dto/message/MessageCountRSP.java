package cn.zswltech.mithras.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 工作台栏目的数量
 *
 * @author zhouning
 * @date 2024/06/20 5:36 PM
 */
@Data
public class MessageCountRSP {

    @ApiModelProperty("总共数量")
    private Integer totalCount;

    @ApiModelProperty("未读数量")
    private Integer unreadCount;
}
