package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingAmountModelQueryRSP {

    @ApiModelProperty(value = "模型编号")
    private String code;

    @ApiModelProperty(value = "模型名称")
    private String name;

}
