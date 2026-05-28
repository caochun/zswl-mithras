package cn.zswltech.mithras.dto.contract.price;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName StructuredInterest
 * @Description 结构化利息
 * @Author jackerhe
 * @Date 2024/4/2 10:19 上午
 * @Version 1.0
 **/
@Data
public class StructuredInterest {
    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("金额")
    private Long amount;
}
