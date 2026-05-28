package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountModelQueryREQ {

    @ApiModelProperty("债项类型：是否为项目制")
    @NotNull(message = "是否为项目制不得为空")
    private Boolean projSystem;

    @ApiModelProperty(value = "评估主体ID")
    private Long evaluationSubjectId;

}
