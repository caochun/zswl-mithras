package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangxiong
 * @date 2024/6/24/19:20
 * @description
 */
@Data
@ApiModel(value = "卡片列表实体")
public class CardListVo {

    @ApiModelProperty(value = "类别")
    private String type;

    @ApiModelProperty(value = "累计金额（万元）")
    private String accumulatedAmount;

    @ApiModelProperty(value = "目标（万元）")
    private String target;

    @ApiModelProperty(value = "完成率")
    private String completionRate;

    // 辅助字段
    private transient BigDecimal targetBD;
}
